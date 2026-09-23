package com.example.model

import android.graphics.RectF

enum class CameraAspectRatio(val displayName: String, val ratioFloat: Float) {
    RATIO_9_16("9:16", 9f / 16f),
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_1_1("1:1", 1f)
}

enum class CameraResolution(val label: String, val width: Int, val height: Int) {
    HD_1080P("HD", 1920, 1080),
    QUAD_HD_2K("2K", 2560, 1440),
    ULTRA_HD_4K("4K", 3840, 2160)
}

enum class FlashMode {
    OFF, ON, TORCH, AUTO
}

enum class CinematicFilter(val displayName: String) {
    NONE("Natural"),
    WARM_FILM("Warm 35mm"),
    CLEAN_STUDIO("Studio Soft"),
    CYBERPUNK("Cyber Glow"),
    MONO("Classic B&W")
}

enum class GreenScreenBackdrop(val title: String, val colorHex: Long) {
    OFF("Off", 0x00000000),
    STUDIO_LOFT("Loft Studio", 0xFF1E2640),
    NEON_OFFICE("Neon Stage", 0xFF2A163B),
    MINIMAL_DESK("Modern Minimal", 0xFF14221D),
    CHROMA_GREEN("Chroma 100%", 0xFF00FF00)
}

enum class ExposureStatus {
    UNDEREXPOSED, OPTIMAL, OVEREXPOSED
}

data class AiDirectorState(
    val hasSubject: Boolean = false,
    val faceBoundsNormalized: RectF? = null,
    val horizontalOffset: Float = 0f,    // -1f (too left) to +1f (too right)
    val verticalOffset: Float = 0f,      // -1f (too high) to +1f (too low)
    val headroomRatio: Float = 0f,       // top of frame to top of head
    val subjectAreaRatio: Float = 0f,    // face size vs frame size
    val isLookingAtCamera: Boolean = true,
    val directorCue: String = "Step in front of camera",
    val isPerfectFrame: Boolean = false,
    val framingScore: Int = 0,
    val countdownRemaining: Int? = null,
    val perfectFrameDurationMs: Long = 0L
)

data class QualityMetrics(
    val luminance: Float = 128f,
    val exposureStatus: ExposureStatus = ExposureStatus.OPTIMAL,
    val sharpnessScore: Float = 85f,
    val isLensSmudged: Boolean = false,
    val isMotionBlurDetected: Boolean = false,
    val audioLevelDb: Float = -18f,
    val estimatedFps: Int = 30
)

data class TeleprompterState(
    val isVisible: Boolean = false,
    val isPlaying: Boolean = false,
    val scriptText: String = "Welcome to FrameAI! When recording alone with the rear camera, our AI director guides your positioning, headroom, and lighting in real time so every take is centered and framed.",
    val scrollSpeed: Float = 1.0f,
    val fontSizeSp: Float = 18f,
    val currentScrollOffset: Float = 0f
)

data class ClipItem(
    val id: String,
    val title: String,
    val durationSeconds: Int,
    val timestampFormatted: String,
    val resolution: String,
    val fps: Int,
    val framingScore: Int
)

data class BackgroundMusic(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val bpm: Int
)
