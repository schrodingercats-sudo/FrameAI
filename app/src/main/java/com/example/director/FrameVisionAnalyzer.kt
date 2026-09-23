package com.example.director

import android.graphics.RectF
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.model.AiDirectorState
import com.example.model.ExposureStatus
import com.example.model.QualityMetrics
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.nio.ByteBuffer
import kotlin.math.abs

class FrameVisionAnalyzer(
    private val onDirectorUpdate: (AiDirectorState) -> Unit,
    private val onQualityUpdate: (QualityMetrics) -> Unit
) : ImageAnalysis.Analyzer {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )

    private var isBusy = false
    private var frameCount = 0L
    private var lastProcessTimeMs = 0L
    private var lastFaceSeenTimeMs = 0L

    // For motion and smudge estimation
    private var previousAverageLuma = 128f
    private var lastMotionWarningTimeMs = 0L
    private var lastLightingWarningTimeMs = 0L
    private var currentExposureStatus = ExposureStatus.OPTIMAL

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        // Throttle inference to ~15-20 FPS to keep UI buttery smooth and conserve battery
        if (currentTime - lastProcessTimeMs < 50) {
            imageProxy.close()
            return
        }
        lastProcessTimeMs = currentTime

        frameCount++
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        // Quick lighting & quality check on Y plane (fast CPU pass)
        analyzeFrameQuality(mediaImage)

        if (isBusy) {
            imageProxy.close()
            return
        }
        isBusy = true

        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                val imageWidth = if (rotationDegrees == 90 || rotationDegrees == 270) {
                    imageProxy.height.toFloat()
                } else {
                    imageProxy.width.toFloat()
                }
                val imageHeight = if (rotationDegrees == 90 || rotationDegrees == 270) {
                    imageProxy.width.toFloat()
                } else {
                    imageProxy.height.toFloat()
                }

                processFaceResults(faces, imageWidth, imageHeight)
            }
            .addOnFailureListener {
                onDirectorUpdate(
                    AiDirectorState(
                        hasSubject = false,
                        directorCue = "Searching for subject..."
                    )
                )
            }
            .addOnCompleteListener {
                isBusy = false
                imageProxy.close()
            }
    }

    private fun processFaceResults(faces: List<Face>, frameW: Float, frameH: Float) {
        val currentTime = System.currentTimeMillis()

        if (faces.isEmpty()) {
            val elapsedSinceFace = currentTime - lastFaceSeenTimeMs
            val cue = if (lastFaceSeenTimeMs > 0 && elapsedSinceFace < 2000L) {
                "Hold position..."
            } else {
                "Step in front of camera"
            }
            onDirectorUpdate(
                AiDirectorState(
                    hasSubject = false,
                    faceBoundsNormalized = null,
                    directorCue = cue,
                    isPerfectFrame = false,
                    framingScore = 0
                )
            )
            return
        }

        lastFaceSeenTimeMs = currentTime

        // If multiple faces are detected, guide for solo creator or group framing
        if (faces.size > 1) {
            // Find overall bounding box for multiple subjects
            val minLeft = faces.minOf { it.boundingBox.left }
            val minTop = faces.minOf { it.boundingBox.top }
            val maxRight = faces.maxOf { it.boundingBox.right }
            val maxBottom = faces.maxOf { it.boundingBox.bottom }

            val normLeft = (minLeft.toFloat() / frameW).coerceIn(0f, 1f)
            val normTop = (minTop.toFloat() / frameH).coerceIn(0f, 1f)
            val normRight = (maxRight.toFloat() / frameW).coerceIn(0f, 1f)
            val normBottom = (maxBottom.toFloat() / frameH).coerceIn(0f, 1f)
            val groupBounds = RectF(normLeft, normTop, normRight, normBottom)

            val groupCenterX = (normLeft + normRight) / 2f
            var groupCue = "Multiple subjects detected - squeeze together"
            if (groupCenterX < 0.40f) {
                groupCue = "Both move to your right"
            } else if (groupCenterX > 0.60f) {
                groupCue = "Both move to your left"
            }

            onDirectorUpdate(
                AiDirectorState(
                    hasSubject = true,
                    faceBoundsNormalized = groupBounds,
                    horizontalOffset = (groupCenterX - 0.5f) * 2f,
                    verticalOffset = 0f,
                    headroomRatio = normTop,
                    subjectAreaRatio = (normRight - normLeft) * (normBottom - normTop),
                    isLookingAtCamera = true,
                    directorCue = groupCue,
                    isPerfectFrame = false,
                    framingScore = 70
                )
            )
            return
        }

        // Single primary subject
        val primaryFace = faces[0]
        val box = primaryFace.boundingBox

        // Normalize bounding box coordinates to 0f..1f range
        val normLeft = (box.left.toFloat() / frameW).coerceIn(0f, 1f)
        val normTop = (box.top.toFloat() / frameH).coerceIn(0f, 1f)
        val normRight = (box.right.toFloat() / frameW).coerceIn(0f, 1f)
        val normBottom = (box.bottom.toFloat() / frameH).coerceIn(0f, 1f)
        val normBounds = RectF(normLeft, normTop, normRight, normBottom)

        val centerX = (normLeft + normRight) / 2f
        val centerY = (normTop + normBottom) / 2f
        val faceWidth = normRight - normLeft
        val faceHeight = normBottom - normTop
        val faceAreaRatio = faceWidth * faceHeight

        // Horizontal offset: -1f (extreme left) to +1f (extreme right). Center is 0.5f.
        val hOffset = (centerX - 0.5f) * 2f
        // Vertical offset: Eye-line target is around 0.35f - 0.40f (golden rule of thirds)
        val targetCenterY = 0.38f
        val vOffset = (centerY - targetCenterY) * 2f

        val headroom = normTop // space above top of head (ideal is ~0.10f..0.22f)

        // Head angles
        val eulerX = primaryFace.headEulerAngleX // Pitch (looking up/down)
        val eulerY = primaryFace.headEulerAngleY // Yaw (looking left/right)
        val eulerZ = primaryFace.headEulerAngleZ // Roll (head tilt)

        // Eye state classification (if available)
        val leftEyeOpen = primaryFace.leftEyeOpenProbability ?: 1.0f
        val rightEyeOpen = primaryFace.rightEyeOpenProbability ?: 1.0f
        val areEyesClosed = leftEyeOpen < 0.25f && rightEyeOpen < 0.25f

        var score = 100
        var cue = "PERFECT FRAME"

        // Direction logic:
        // When user is facing the REAR camera, their perspective is mirrored compared to the camera sensor.
        // If centerX < 0.40f (user appears on left of camera image), they need to shift toward THEIR right.
        if (centerX < 0.40f) {
            cue = "Move to your right"
            score -= (abs(centerX - 0.5f) * 80).toInt()
        } else if (centerX > 0.60f) {
            cue = "Move to your left"
            score -= (abs(centerX - 0.5f) * 80).toInt()
        } else if (faceAreaRatio < 0.040f) {
            // Subject is too far away
            cue = "Step closer"
            score -= 25
        } else if (faceAreaRatio > 0.32f) {
            // Subject is too close / cropped
            cue = "Step back"
            score -= 25
        } else if (headroom < 0.06f) {
            // Cut off at the top
            cue = "Lower camera or step back"
            score -= 20
        } else if (headroom > 0.28f) {
            // Too much empty sky above head
            cue = "Raise your head or move closer"
            score -= 15
        } else if (abs(eulerZ) > 16f) {
            // Head tilt
            cue = "Keep your head straight"
            score -= 10
        } else if (eulerX < -14f) {
            cue = "Raise your chin"
            score -= 15
        } else if (eulerX > 16f) {
            cue = "Lower your chin"
            score -= 15
        } else if (abs(eulerY) > 18f) {
            cue = "Look directly at camera"
            score -= 15
        } else if (areEyesClosed) {
            cue = "Keep eyes open"
            score -= 10
        } else if (currentExposureStatus == ExposureStatus.UNDEREXPOSED && (currentTime - lastLightingWarningTimeMs) > 6000L) {
            lastLightingWarningTimeMs = currentTime
            cue = "Face is too dark, face the light"
            score -= 15
        }

        val isLooking = abs(eulerY) < 15f && abs(eulerX) < 14f
        val isPerfect = cue == "PERFECT FRAME" && score >= 88

        onDirectorUpdate(
            AiDirectorState(
                hasSubject = true,
                faceBoundsNormalized = normBounds,
                horizontalOffset = hOffset,
                verticalOffset = vOffset,
                headroomRatio = headroom,
                subjectAreaRatio = faceAreaRatio,
                isLookingAtCamera = isLooking,
                directorCue = cue,
                isPerfectFrame = isPerfect,
                framingScore = score.coerceIn(0, 100)
            )
        )
    }

    private fun analyzeFrameQuality(mediaImage: Image) {
        try {
            val yPlane = mediaImage.planes[0]
            val buffer: ByteBuffer = yPlane.buffer
            val rowStride = yPlane.rowStride
            val pixelStride = yPlane.pixelStride
            val width = mediaImage.width
            val height = mediaImage.height

            var sumLuma = 0L
            var sampleCount = 0
            val step = 16 // sample every 16th pixel for high speed

            for (y in 0 until height step step) {
                val rowOffset = y * rowStride
                for (x in 0 until width step step) {
                    val index = rowOffset + x * pixelStride
                    if (index < buffer.limit()) {
                        val luma = buffer.get(index).toInt() and 0xFF
                        sumLuma += luma
                        sampleCount++
                    }
                }
            }

            val avgLuma = if (sampleCount > 0) sumLuma.toFloat() / sampleCount else 128f
            val exposureStatus = when {
                avgLuma < 45f -> ExposureStatus.UNDEREXPOSED
                avgLuma > 218f -> ExposureStatus.OVEREXPOSED
                else -> ExposureStatus.OPTIMAL
            }
            currentExposureStatus = exposureStatus

            val deltaLuma = abs(avgLuma - previousAverageLuma)
            previousAverageLuma = avgLuma
            val motionDetected = deltaLuma > 24f

            onQualityUpdate(
                QualityMetrics(
                    luminance = avgLuma,
                    exposureStatus = exposureStatus,
                    sharpnessScore = (88f - (deltaLuma * 0.8f)).coerceIn(40f, 98f),
                    isLensSmudged = false,
                    isMotionBlurDetected = motionDetected,
                    estimatedFps = 30
                )
            )
        } catch (e: Exception) {
            // Ignore analysis sampling error
        }
    }
}
