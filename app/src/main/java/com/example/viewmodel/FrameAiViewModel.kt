package com.example.viewmodel

import android.app.Application
import android.graphics.RectF
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.director.DirectorVoiceManager
import com.example.director.DirectorVoicePersona
import com.example.model.AiDirectorState
import com.example.model.BackgroundMusic
import com.example.model.CameraAspectRatio
import com.example.model.CameraResolution
import com.example.model.CinematicFilter
import com.example.model.ClipItem
import com.example.model.ExposureStatus
import com.example.model.FlashMode
import com.example.model.GreenScreenBackdrop
import com.example.model.QualityMetrics
import com.example.model.TeleprompterState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

enum class BottomTab {
    MEDIA, CLIPS, MODES, DIRECTOR, ANALYTICS
}

class FrameAiViewModel(application: Application) : AndroidViewModel(application) {

    val voiceManager = DirectorVoiceManager(application)

    // Camera settings state
    private val _aspectRatio = MutableStateFlow(CameraAspectRatio.RATIO_9_16)
    val aspectRatio: StateFlow<CameraAspectRatio> = _aspectRatio.asStateFlow()

    private val _zoomRatio = MutableStateFlow(1.0f)
    val zoomRatio: StateFlow<Float> = _zoomRatio.asStateFlow()

    private val _resolution = MutableStateFlow(CameraResolution.HD_1080P)
    val resolution: StateFlow<CameraResolution> = _resolution.asStateFlow()

    private val _fps = MutableStateFlow(30)
    val fps: StateFlow<Int> = _fps.asStateFlow()

    private val _isHdr = MutableStateFlow(false)
    val isHdr: StateFlow<Boolean> = _isHdr.asStateFlow()

    private val _flashMode = MutableStateFlow(FlashMode.OFF)
    val flashMode: StateFlow<FlashMode> = _flashMode.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _speed = MutableStateFlow(1.0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isAutoFramingEnabled = MutableStateFlow(true)
    val isAutoFramingEnabled: StateFlow<Boolean> = _isAutoFramingEnabled.asStateFlow()

    private val _isVoiceDirectorEnabled = MutableStateFlow(true)
    val isVoiceDirectorEnabled: StateFlow<Boolean> = _isVoiceDirectorEnabled.asStateFlow()

    private val _voicePersona = MutableStateFlow(DirectorVoicePersona.HOLLYWOOD_DIRECTOR)
    val voicePersona: StateFlow<DirectorVoicePersona> = _voicePersona.asStateFlow()

    private val _isAutoEnhanceEnabled = MutableStateFlow(true)
    val isAutoEnhanceEnabled: StateFlow<Boolean> = _isAutoEnhanceEnabled.asStateFlow()

    private val _cinematicFilter = MutableStateFlow(CinematicFilter.NONE)
    val cinematicFilter: StateFlow<CinematicFilter> = _cinematicFilter.asStateFlow()

    private val _greenScreenBackdrop = MutableStateFlow(GreenScreenBackdrop.OFF)
    val greenScreenBackdrop: StateFlow<GreenScreenBackdrop> = _greenScreenBackdrop.asStateFlow()

    private val _selectedMusic = MutableStateFlow<BackgroundMusic?>(null)
    val selectedMusic: StateFlow<BackgroundMusic?> = _selectedMusic.asStateFlow()

    // Director & Quality State
    private val _directorState = MutableStateFlow(AiDirectorState())
    val directorState: StateFlow<AiDirectorState> = _directorState.asStateFlow()

    private val _qualityMetrics = MutableStateFlow(QualityMetrics())
    val qualityMetrics: StateFlow<QualityMetrics> = _qualityMetrics.asStateFlow()

    // Recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _countdownActive = MutableStateFlow(false)
    val countdownActive: StateFlow<Boolean> = _countdownActive.asStateFlow()

    private val _countdownValue = MutableStateFlow(3)
    val countdownValue: StateFlow<Int> = _countdownValue.asStateFlow()

    // Audio & Teleprompter
    private val _isAudioMuted = MutableStateFlow(false)
    val isAudioMuted: StateFlow<Boolean> = _isAudioMuted.asStateFlow()

    private val _audioInputLevel = MutableStateFlow(0.72f) // 0f..1f slider / meter
    val audioInputLevel: StateFlow<Float> = _audioInputLevel.asStateFlow()

    private val _teleprompter = MutableStateFlow(TeleprompterState())
    val teleprompter: StateFlow<TeleprompterState> = _teleprompter.asStateFlow()

    // Bottom Navigation
    private val _currentTab = MutableStateFlow(BottomTab.DIRECTOR)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    // UI Dialogs
    private val _showResolutionSheet = MutableStateFlow(false)
    val showResolutionSheet: StateFlow<Boolean> = _showResolutionSheet.asStateFlow()

    private val _showMusicSheet = MutableStateFlow(false)
    val showMusicSheet: StateFlow<Boolean> = _showMusicSheet.asStateFlow()

    private val _showGreenScreenSheet = MutableStateFlow(false)
    val showGreenScreenSheet: StateFlow<Boolean> = _showGreenScreenSheet.asStateFlow()

    private val _showTimerSheet = MutableStateFlow(false)
    val showTimerSheet: StateFlow<Boolean> = _showTimerSheet.asStateFlow()

    private val _showSpeedSheet = MutableStateFlow(false)
    val showSpeedSheet: StateFlow<Boolean> = _showSpeedSheet.asStateFlow()

    private val _showEnhanceSheet = MutableStateFlow(false)
    val showEnhanceSheet: StateFlow<Boolean> = _showEnhanceSheet.asStateFlow()

    private val _showVoiceSettingsSheet = MutableStateFlow(false)
    val showVoiceSettingsSheet: StateFlow<Boolean> = _showVoiceSettingsSheet.asStateFlow()

    // Focus point reticle
    private val _focusPoint = MutableStateFlow<Pair<Float, Float>?>(null)
    val focusPoint: StateFlow<Pair<Float, Float>?> = _focusPoint.asStateFlow()

    // Recorded Clips
    private val _clips = MutableStateFlow<List<ClipItem>>(listOf(
        ClipItem("1", "Take 1 - Intro Hook", 14, "Today, 11:42 AM", "1080p", 30, 94),
        ClipItem("2", "Take 2 - Main Explanation", 42, "Today, 11:48 AM", "1080p", 30, 98)
    ))
    val clips: StateFlow<List<ClipItem>> = _clips.asStateFlow()

    private var recordingJob: Job? = null
    private var countdownJob: Job? = null
    private var perfectFrameStartTime: Long = 0L
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    init {
        // Start simulated audio pulse for live recording meter
        viewModelScope.launch {
            while (true) {
                delay(120)
                if (_isRecording.value && !_isAudioMuted.value) {
                    val variation = ((System.currentTimeMillis() % 1000) / 1000f) * 0.35f
                    val newLevel = (0.55f + variation).coerceIn(0.2f, 0.95f)
                    _audioInputLevel.value = newLevel
                }
            }
        }
    }

    fun bindCamera(control: CameraControl?, info: CameraInfo?) {
        this.cameraControl = control
        this.cameraInfo = info
        applyZoom(_zoomRatio.value)
        applyTorch(_flashMode.value == FlashMode.TORCH)
    }

    fun onDirectorUpdate(state: AiDirectorState) {
        _directorState.value = state

        if (_isVoiceDirectorEnabled.value) {
            voiceManager.setEnabled(true)
            if (state.hasSubject) {
                voiceManager.speakDirectorCue(state.directorCue)
            }
        } else {
            voiceManager.setEnabled(false)
        }

        // Handle Perfect Frame detection
        if (state.isPerfectFrame) {
            if (perfectFrameStartTime == 0L) {
                perfectFrameStartTime = System.currentTimeMillis()
                voiceManager.playPerfectFrameChime()
                voiceManager.speakDirectorCue("Hold position, perfect frame!", isUrgent = true)
            }
            // If sustained for 1.8 seconds, trigger auto countdown or take if configured
            val sustainedMs = System.currentTimeMillis() - perfectFrameStartTime
            if (sustainedMs > 1800L && !_isRecording.value && !_countdownActive.value && _timerSeconds.value > 0) {
                startCountdown()
            }
        } else {
            perfectFrameStartTime = 0L
        }

        // Smart Auto-Framing: smoothly adjust zoom if enabled
        if (_isAutoFramingEnabled.value && state.hasSubject && !_isRecording.value) {
            adjustAutoFraming(state)
        }
    }

    private fun adjustAutoFraming(state: AiDirectorState) {
        // If subject is too far away, digitally zoom in slightly
        if (state.subjectAreaRatio in 0.01f..0.04f && _zoomRatio.value < 1.8f) {
            val nextZoom = (_zoomRatio.value + 0.05f).coerceAtMost(2.0f)
            setZoom(nextZoom)
        } else if (state.subjectAreaRatio > 0.28f && _zoomRatio.value > 1.0f) {
            val nextZoom = (_zoomRatio.value - 0.05f).coerceAtLeast(0.6f)
            setZoom(nextZoom)
        }
    }

    fun onQualityUpdate(metrics: QualityMetrics) {
        _qualityMetrics.value = metrics
        if (_isAutoEnhanceEnabled.value) {
            // Apply auto exposure compensation through CameraControl
            try {
                if (metrics.exposureStatus == ExposureStatus.UNDEREXPOSED) {
                    cameraControl?.setExposureCompensationIndex(1)
                } else if (metrics.exposureStatus == ExposureStatus.OVEREXPOSED) {
                    cameraControl?.setExposureCompensationIndex(-1)
                } else {
                    cameraControl?.setExposureCompensationIndex(0)
                }
            } catch (e: Exception) {
                // Ignore if device does not support exposure compensation
            }
        }
    }

    fun setZoom(ratio: Float) {
        _zoomRatio.value = ratio
        applyZoom(ratio)
    }

    private fun applyZoom(ratio: Float) {
        try {
            cameraControl?.setZoomRatio(ratio)
        } catch (e: Exception) {
            // Ignore zoom hardware constraint
        }
    }

    fun setAspectRatio(ratio: CameraAspectRatio) {
        _aspectRatio.value = ratio
    }

    fun setResolution(res: CameraResolution, newFps: Int) {
        _resolution.value = res
        _fps.value = newFps
        _showResolutionSheet.value = false
    }

    fun toggleHdr() {
        _isHdr.value = !_isHdr.value
    }

    fun toggleFlash() {
        val next = when (_flashMode.value) {
            FlashMode.OFF -> FlashMode.TORCH
            FlashMode.TORCH -> FlashMode.AUTO
            FlashMode.AUTO -> FlashMode.OFF
            FlashMode.ON -> FlashMode.OFF
        }
        _flashMode.value = next
        applyTorch(next == FlashMode.TORCH)
    }

    private fun applyTorch(enable: Boolean) {
        try {
            cameraControl?.enableTorch(enable)
        } catch (e: Exception) {
            // Ignore torch exception
        }
    }

    fun toggleCameraFacing() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun setSpeed(spd: Float) {
        _speed.value = spd
        _showSpeedSheet.value = false
    }

    fun setTimer(seconds: Int) {
        _timerSeconds.value = seconds
        _showTimerSheet.value = false
    }

    fun toggleAutoFraming() {
        _isAutoFramingEnabled.value = !_isAutoFramingEnabled.value
    }

    fun toggleVoiceDirector() {
        val newVal = !_isVoiceDirectorEnabled.value
        _isVoiceDirectorEnabled.value = newVal
        voiceManager.setEnabled(newVal)
        if (newVal) {
            voiceManager.speakDirectorCue("Voice director active", isUrgent = true)
        }
    }

    fun setVoicePersona(persona: DirectorVoicePersona) {
        _voicePersona.value = persona
        voiceManager.setPersona(persona)
        voiceManager.speakDirectorCue("Voice updated to ${persona.displayName}", isUrgent = true)
        _showVoiceSettingsSheet.value = false
    }

    fun toggleAutoEnhance() {
        _isAutoEnhanceEnabled.value = !_isAutoEnhanceEnabled.value
    }

    fun setCinematicFilter(filter: CinematicFilter) {
        _cinematicFilter.value = filter
    }

    fun setGreenScreen(backdrop: GreenScreenBackdrop) {
        _greenScreenBackdrop.value = backdrop
        _showGreenScreenSheet.value = false
    }

    fun selectMusic(music: BackgroundMusic?) {
        _selectedMusic.value = music
        _showMusicSheet.value = false
    }

    fun setAudioLevel(level: Float) {
        _audioInputLevel.value = level.coerceIn(0f, 1f)
    }

    fun toggleAudioMute() {
        _isAudioMuted.value = !_isAudioMuted.value
    }

    fun triggerShutterClick() {
        if (_isRecording.value) {
            stopRecording()
        } else {
            if (_timerSeconds.value > 0) {
                startCountdown()
            } else {
                startRecording()
            }
        }
    }

    private fun startCountdown() {
        if (_countdownActive.value) return
        _countdownActive.value = true
        _countdownValue.value = if (_timerSeconds.value > 0) _timerSeconds.value else 3

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_countdownValue.value > 0) {
                voiceManager.playCountdownBeep(_countdownValue.value)
                voiceManager.speakDirectorCue("${_countdownValue.value}", isUrgent = true)
                delay(1000)
                _countdownValue.value -= 1
            }
            _countdownActive.value = false
            voiceManager.playCountdownBeep(0)
            startRecording()
        }
    }

    private fun startRecording() {
        _isRecording.value = true
        _recordingDurationSeconds.value = 0
        voiceManager.speakDirectorCue("Action! Recording started", isUrgent = true)

        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingDurationSeconds.value += 1
            }
        }
    }

    private fun stopRecording() {
        val duration = _recordingDurationSeconds.value
        _isRecording.value = false
        recordingJob?.cancel()
        voiceManager.speakDirectorCue("Cut! Take saved", isUrgent = true)

        if (duration >= 1) {
            val df = SimpleDateFormat("h:mm a", Locale.getDefault())
            val newClip = ClipItem(
                id = System.currentTimeMillis().toString(),
                title = "Take ${_clips.value.size + 1} - ${_directorState.value.directorCue}",
                durationSeconds = duration,
                timestampFormatted = "Today, ${df.format(Date())}",
                resolution = _resolution.value.label,
                fps = _fps.value,
                framingScore = _directorState.value.framingScore
            )
            _clips.update { listOf(newClip) + it }
        }
    }

    fun toggleTeleprompter() {
        _teleprompter.update { it.copy(isVisible = !it.isVisible) }
    }

    fun updateTeleprompterText(newText: String) {
        _teleprompter.update { it.copy(scriptText = newText) }
    }

    fun toggleTeleprompterPlay() {
        _teleprompter.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun setTeleprompterSpeed(speed: Float) {
        _teleprompter.update { it.copy(scrollSpeed = speed) }
    }

    fun setFocusPoint(x: Float, y: Float) {
        _focusPoint.value = Pair(x, y)
        viewModelScope.launch {
            delay(2200)
            if (_focusPoint.value == Pair(x, y)) {
                _focusPoint.value = null
            }
        }
    }

    fun setCurrentTab(tab: BottomTab) {
        _currentTab.value = tab
    }

    // Dialog toggles
    fun setShowResolutionSheet(show: Boolean) { _showResolutionSheet.value = show }
    fun setShowMusicSheet(show: Boolean) { _showMusicSheet.value = show }
    fun setShowGreenScreenSheet(show: Boolean) { _showGreenScreenSheet.value = show }
    fun setShowTimerSheet(show: Boolean) { _showTimerSheet.value = show }
    fun setShowSpeedSheet(show: Boolean) { _showSpeedSheet.value = show }
    fun setShowEnhanceSheet(show: Boolean) { _showEnhanceSheet.value = show }
    fun setShowVoiceSettingsSheet(show: Boolean) { _showVoiceSettingsSheet.value = show }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
        applyTorch(false)
    }
}
