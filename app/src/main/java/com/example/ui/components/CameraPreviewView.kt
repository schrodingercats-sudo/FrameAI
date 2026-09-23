package com.example.ui.components

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.director.FrameVisionAnalyzer
import com.example.model.CinematicFilter
import com.example.model.GreenScreenBackdrop
import com.example.viewmodel.FrameAiViewModel
import java.util.concurrent.Executors

@Composable
fun CameraPreviewView(
    viewModel: FrameAiViewModel,
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean = false,
    filter: CinematicFilter = CinematicFilter.NONE,
    backdrop: GreenScreenBackdrop = GreenScreenBackdrop.OFF,
    focusPoint: Pair<Float, Float>? = null,
    onTapToFocus: (Float, Float) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isCameraBound by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val analyzer = remember {
        FrameVisionAnalyzer(
            onDirectorUpdate = { directorState ->
                viewModel.onDirectorUpdate(directorState)
            },
            onQualityUpdate = { qualityMetrics ->
                viewModel.onQualityUpdate(qualityMetrics)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onTapToFocus(offset.x, offset.y)
                }
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()

                        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

                        val cameraSelector = if (isFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )

                        viewModel.bindCamera(camera.cameraControl, camera.cameraInfo)
                        isCameraBound = true
                    } catch (e: Exception) {
                        Log.e("CameraPreviewView", "Camera binding failed", e)
                        isCameraBound = false
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            update = {
                // Trigger updates if camera direction changes
            }
        )

        // Green screen or studio backdrop simulation overlay if active
        if (backdrop != GreenScreenBackdrop.OFF) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(backdrop.colorHex).copy(alpha = 0.55f))
            )
        }

        // Cinematic color grading tone curve overlay
        when (filter) {
            CinematicFilter.WARM_FILM -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x1AFFB03B))
                )
            }
            CinematicFilter.CLEAN_STUDIO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x103B82F6))
                )
            }
            CinematicFilter.CYBERPUNK -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x1C8B5CF6))
                )
            }
            CinematicFilter.MONO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                )
            }
            CinematicFilter.NONE -> Unit
        }

        // Tap-to-focus ring reticle (matching Screenshot 4 double ring design)
        focusPoint?.let { (fx, fy) ->
            FocusRingIndicator(x = fx, y = fy)
        }
    }
}

@Composable
fun FocusRingIndicator(x: Float, y: Float) {
    Box(
        modifier = Modifier
            .offset(x = (x - 40).dp, y = (y - 40).dp)
            .size(80.dp)
            .clip(CircleShape)
            .background(Color(0x1AFFFFFF))
    ) {
        // Inner white ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        )
    }
}
