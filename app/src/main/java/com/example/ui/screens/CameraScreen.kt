package com.example.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraAspectRatio
import com.example.model.CinematicFilter
import com.example.ui.components.AnalyticsView
import com.example.ui.components.BottomShutterBar
import com.example.ui.components.CameraPreviewView
import com.example.ui.components.DirectorOverlay
import com.example.ui.components.FrameAiBottomNav
import com.example.ui.components.GreenScreenSheet
import com.example.ui.components.LeftToolBar
import com.example.ui.components.MediaClipsView
import com.example.ui.components.MusicSheet
import com.example.ui.components.RecordingControlsBar
import com.example.ui.components.ResolutionSheet
import com.example.ui.components.SimpleOptionSheet
import com.example.ui.components.TeleprompterOverlay
import com.example.ui.components.TopRecordingPill
import com.example.ui.components.ZoomArcSelector
import com.example.ui.components.liquidGlass
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.IosObsidian
import com.example.ui.theme.ShutterPink
import com.example.viewmodel.BottomTab
import com.example.viewmodel.FrameAiViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: FrameAiViewModel,
    modifier: Modifier = Modifier
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val hasCameraPermission = permissionsState.permissions.firstOrNull { it.permission == Manifest.permission.CAMERA }?.status?.isGranted ?: false

    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingDurationSeconds.collectAsState()
    val countdownActive by viewModel.countdownActive.collectAsState()
    val countdownValue by viewModel.countdownValue.collectAsState()

    val currentZoom by viewModel.zoomRatio.collectAsState()
    val currentRes by viewModel.resolution.collectAsState()
    val currentFps by viewModel.fps.collectAsState()
    val isHdr by viewModel.isHdr.collectAsState()
    val flashMode by viewModel.flashMode.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val speed by viewModel.speed.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isAutoEnhance by viewModel.isAutoEnhanceEnabled.collectAsState()
    val cinematicFilter by viewModel.cinematicFilter.collectAsState()
    val greenScreen by viewModel.greenScreenBackdrop.collectAsState()
    val selectedMusic by viewModel.selectedMusic.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()

    val directorState by viewModel.directorState.collectAsState()
    val qualityMetrics by viewModel.qualityMetrics.collectAsState()
    val teleprompterState by viewModel.teleprompter.collectAsState()
    val audioLevel by viewModel.audioInputLevel.collectAsState()
    val isAudioMuted by viewModel.isAudioMuted.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val clips by viewModel.clips.collectAsState()
    val isVoiceDirector by viewModel.isVoiceDirectorEnabled.collectAsState()
    val voicePersona by viewModel.voicePersona.collectAsState()
    val isAutoFraming by viewModel.isAutoFramingEnabled.collectAsState()

    // Dialog sheets
    val showResSheet by viewModel.showResolutionSheet.collectAsState()
    val showMusicSheet by viewModel.showMusicSheet.collectAsState()
    val showGreenScreenSheet by viewModel.showGreenScreenSheet.collectAsState()
    val showTimerSheet by viewModel.showTimerSheet.collectAsState()
    val showSpeedSheet by viewModel.showSpeedSheet.collectAsState()
    val showEnhanceSheet by viewModel.showEnhanceSheet.collectAsState()
    val showVoiceSheet by viewModel.showVoiceSettingsSheet.collectAsState()

    val focusPoint by viewModel.focusPoint.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IosObsidian)
    ) {
        if (!hasCameraPermission) {
            // Permission request screen
            CameraPermissionPrompt(onRequest = { permissionsState.launchMultiplePermissionRequest() })
            return@Box
        }

        // Main Viewfinder Container (Screenshots 1-4 with rounded camera card viewport)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 58.dp) // Space for bottom navigation
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(Color.Black)
        ) {
            // 1. Live Camera Preview (CameraX)
            CameraPreviewView(
                viewModel = viewModel,
                isFrontCamera = isFrontCamera,
                filter = cinematicFilter,
                backdrop = greenScreen,
                focusPoint = focusPoint,
                onTapToFocus = { x, y ->
                    viewModel.setFocusPoint(x, y)
                }
            )

            // 2. AI Director Overlay (Reticle, golden headroom guide, perfect frame banner)
            DirectorOverlay(
                directorState = directorState,
                countdownActive = countdownActive,
                countdownValue = countdownValue,
                showGrid = !isRecording
            )

            // 3. Top Section (Status / Recording Timer)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 10.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    TopRecordingPill(durationSeconds = recordingSeconds)
                } else {
                    // Aspect ratio selector pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x661E2430))
                            .clickable {
                                val nextRatio = when (aspectRatio) {
                                    CameraAspectRatio.RATIO_9_16 -> CameraAspectRatio.RATIO_16_9
                                    CameraAspectRatio.RATIO_16_9 -> CameraAspectRatio.RATIO_1_1
                                    CameraAspectRatio.RATIO_1_1 -> CameraAspectRatio.RATIO_9_16
                                }
                                viewModel.setAspectRatio(nextRatio)
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = aspectRatio.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Floating Teleprompter (if opened)
            TeleprompterOverlay(
                state = teleprompterState,
                onTogglePlay = { viewModel.toggleTeleprompterPlay() },
                onSpeedChange = { viewModel.setTeleprompterSpeed(it) },
                onTextChange = { viewModel.updateTeleprompterText(it) },
                onClose = { viewModel.toggleTeleprompter() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 50.dp)
            )

            // 5. Left ToolBar (Music, Green Screen, Timer, Speed, Teleprompter)
            LeftToolBar(
                isVisible = !isRecording,
                selectedMusic = selectedMusic,
                greenScreenMode = greenScreen,
                timerSeconds = timerSeconds,
                speed = speed,
                isTeleprompterActive = teleprompterState.isVisible,
                onMusicClick = { viewModel.setShowMusicSheet(true) },
                onGreenScreenClick = { viewModel.setShowGreenScreenSheet(true) },
                onTimerClick = { viewModel.setShowTimerSheet(true) },
                onSpeedClick = { viewModel.setShowSpeedSheet(true) },
                onTeleprompterClick = { viewModel.toggleTeleprompter() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 60.dp)
            )

            // 6. Right-Side Recording Controls (Screenshot 3)
            RecordingControlsBar(
                isVisible = isRecording,
                audioLevel = audioLevel,
                isMuted = isAudioMuted,
                onMuteToggle = { viewModel.toggleAudioMute() },
                onOptionsClick = { viewModel.setShowResolutionSheet(true) },
                onSubtitlesClick = { viewModel.toggleTeleprompter() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = 40.dp)
            )

            // 7. Bottom Controls Group (Zoom Arc + Shutter Bar)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Curved Zoom Arc Selector (.6x, 1x, 2x, 5x)
                ZoomArcSelector(
                    currentZoom = currentZoom,
                    onZoomSelected = { viewModel.setZoom(it) }
                )

                // Shutter Bar
                BottomShutterBar(
                    isRecording = isRecording,
                    flashMode = flashMode,
                    isAutoEnhanceActive = isAutoEnhance,
                    resolution = currentRes,
                    fps = currentFps,
                    onFlashToggle = { viewModel.toggleFlash() },
                    onAutoEnhanceToggle = { viewModel.setShowEnhanceSheet(true) },
                    onShutterClick = { viewModel.triggerShutterClick() },
                    onCameraFlip = { viewModel.toggleCameraFacing() },
                    onResolutionClick = { viewModel.setShowResolutionSheet(true) }
                )
            }
        }

        // 8. Bottom Navigation Bar (Media, Clips, Modes, AI Director, Analytics)
        FrameAiBottomNav(
            currentTab = currentTab,
            onTabSelected = { viewModel.setCurrentTab(it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 9. Overlay Tab Views (Media, Analytics, etc.)
        when (currentTab) {
            BottomTab.MEDIA -> {
                MediaClipsView(
                    clips = clips,
                    onClose = { viewModel.setCurrentTab(BottomTab.DIRECTOR) },
                    modifier = Modifier.padding(bottom = 58.dp)
                )
            }
            BottomTab.ANALYTICS -> {
                AnalyticsView(
                    directorState = directorState,
                    qualityMetrics = qualityMetrics,
                    isVoiceDirector = isVoiceDirector,
                    onToggleVoice = { viewModel.toggleVoiceDirector() },
                    voicePersona = voicePersona,
                    onOpenVoiceSettings = { viewModel.setShowVoiceSettingsSheet(true) },
                    isAutoFraming = isAutoFraming,
                    onToggleAutoFraming = { viewModel.toggleAutoFraming() },
                    onClose = { viewModel.setCurrentTab(BottomTab.DIRECTOR) },
                    modifier = Modifier.padding(bottom = 58.dp)
                )
            }
            BottomTab.CLIPS -> {
                MediaClipsView(
                    clips = clips,
                    onClose = { viewModel.setCurrentTab(BottomTab.DIRECTOR) },
                    modifier = Modifier.padding(bottom = 58.dp)
                )
            }
            BottomTab.MODES -> {
                AnalyticsView(
                    directorState = directorState,
                    qualityMetrics = qualityMetrics,
                    isVoiceDirector = isVoiceDirector,
                    onToggleVoice = { viewModel.toggleVoiceDirector() },
                    voicePersona = voicePersona,
                    onOpenVoiceSettings = { viewModel.setShowVoiceSettingsSheet(true) },
                    isAutoFraming = isAutoFraming,
                    onToggleAutoFraming = { viewModel.toggleAutoFraming() },
                    onClose = { viewModel.setCurrentTab(BottomTab.DIRECTOR) },
                    modifier = Modifier.padding(bottom = 58.dp)
                )
            }
            BottomTab.DIRECTOR -> Unit
        }

        // 10. Liquid Glass Bottom Sheets / Popups
        ResolutionSheet(
            isOpen = showResSheet,
            currentRes = currentRes,
            currentFps = currentFps,
            isHdr = isHdr,
            aspectRatio = aspectRatio,
            onResChange = { res, fps -> viewModel.setResolution(res, fps) },
            onHdrToggle = { viewModel.toggleHdr() },
            onRatioChange = { viewModel.setAspectRatio(it) },
            onDismiss = { viewModel.setShowResolutionSheet(false) }
        )

        MusicSheet(
            isOpen = showMusicSheet,
            selectedMusic = selectedMusic,
            onSelectMusic = { viewModel.selectMusic(it) },
            onDismiss = { viewModel.setShowMusicSheet(false) }
        )

        GreenScreenSheet(
            isOpen = showGreenScreenSheet,
            currentBackdrop = greenScreen,
            onSelectBackdrop = { viewModel.setGreenScreen(it) },
            onDismiss = { viewModel.setShowGreenScreenSheet(false) }
        )

        SimpleOptionSheet(
            title = "Recording Timer",
            isOpen = showTimerSheet,
            options = listOf("Off (Instant)", "3 Seconds", "5 Seconds", "10 Seconds"),
            selectedIndex = when (timerSeconds) {
                3 -> 1
                5 -> 2
                10 -> 3
                else -> 0
            },
            onSelect = { idx ->
                val sec = when (idx) {
                    1 -> 3
                    2 -> 5
                    3 -> 10
                    else -> 0
                }
                viewModel.setTimer(sec)
            },
            onDismiss = { viewModel.setShowTimerSheet(false) }
        )

        SimpleOptionSheet(
            title = "Recording Speed",
            isOpen = showSpeedSheet,
            options = listOf("0.5x Slow Motion", "1.0x Standard", "2.0x Fast Motion", "3.0x Hyperlapse"),
            selectedIndex = when (speed) {
                0.5f -> 0
                2.0f -> 2
                3.0f -> 3
                else -> 1
            },
            onSelect = { idx ->
                val spd = when (idx) {
                    0 -> 0.5f
                    2 -> 2.0f
                    3 -> 3.0f
                    else -> 1.0f
                }
                viewModel.setSpeed(spd)
            },
            onDismiss = { viewModel.setShowSpeedSheet(false) }
        )

        SimpleOptionSheet(
            title = "Cinematic Tone & Enhancement",
            isOpen = showEnhanceSheet,
            options = listOf("Natural Standard", "Warm 35mm Film", "Studio Soft Glamour", "Cyberpunk Neon Glow", "Classic Black & White"),
            selectedIndex = cinematicFilter.ordinal,
            onSelect = { idx ->
                viewModel.setCinematicFilter(CinematicFilter.values()[idx])
                viewModel.setShowEnhanceSheet(false)
            },
            onDismiss = { viewModel.setShowEnhanceSheet(false) }
        )

        SimpleOptionSheet(
            title = "AI Director Voice Persona",
            isOpen = showVoiceSheet,
            options = listOf(
                "Hollywood Director (Action & Cut style)",
                "Friendly Coach (Upbeat & Encouraging)",
                "Studio Pro (Precise broadcast style)",
                "Audio Cues & Chimes Only (Minimal tones)"
            ),
            selectedIndex = voicePersona.ordinal,
            onSelect = { idx ->
                viewModel.setVoicePersona(com.example.director.DirectorVoicePersona.values()[idx])
            },
            onDismiss = { viewModel.setShowVoiceSettingsSheet(false) }
        )
    }
}

@Composable
fun CameraPermissionPrompt(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IosObsidian)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShutterPink.copy(alpha = 0.15f))
                    .padding(24.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Permission",
                    tint = ShutterPink,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Text(
                text = "Camera & Mic Access Required",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "FrameAI uses real-time computer vision and on-device machine learning to guide your framing and audio when recording with the rear camera.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = ShutterPink),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Grant Permissions",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
