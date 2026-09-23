package com.example.director

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

enum class DirectorVoicePersona(val displayName: String, val speechRate: Float, val pitch: Float) {
    HOLLYWOOD_DIRECTOR("Hollywood Director", 1.05f, 0.95f),
    FRIENDLY_COACH("Friendly Coach", 1.15f, 1.10f),
    STUDIO_PRO("Studio Pro", 1.00f, 1.00f),
    MINIMAL_BEEPS("Audio Cues & Chimes Only", 1.0f, 1.0f)
}

class DirectorVoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var isEnabled = true
    private var currentPersona = DirectorVoicePersona.HOLLYWOOD_DIRECTOR

    private var lastSpokenCue: String = ""
    private var lastSpokenTimeMs: Long = 0L
    private val debounceIntervalMs = 2400L

    private var toneGenerator: ToneGenerator? = null
    private var vibrator: Vibrator? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)

            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e("DirectorVoice", "Failed to init TTS, ToneGenerator, or Vibrator", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                applyPersonaSettings()
                isTtsReady = true
            }
        }
    }

    fun setPersona(persona: DirectorVoicePersona) {
        currentPersona = persona
        applyPersonaSettings()
    }

    fun getPersona(): DirectorVoicePersona = currentPersona

    private fun applyPersonaSettings() {
        tts?.setSpeechRate(currentPersona.speechRate)
        tts?.setPitch(currentPersona.pitch)
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            tts?.stop()
        }
    }

    fun speakDirectorCue(cue: String, isUrgent: Boolean = false) {
        if (!isEnabled) return

        val now = System.currentTimeMillis()

        // Debounce to prevent voice clutter while user is adjusting position
        if (!isUrgent && cue == lastSpokenCue && (now - lastSpokenTimeMs) < 4000L) {
            return
        }

        if (!isUrgent && (now - lastSpokenTimeMs) < debounceIntervalMs) {
            return
        }

        lastSpokenCue = cue
        lastSpokenTimeMs = now

        // If persona is minimal beeps, produce audio tone instead of voice
        if (currentPersona == DirectorVoicePersona.MINIMAL_BEEPS && !isUrgent) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
            return
        }

        if (isTtsReady) {
            try {
                tts?.speak(cue, TextToSpeech.QUEUE_FLUSH, null, "DirectorCue_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e("DirectorVoice", "Speech error", e)
            }
        }
    }

    fun playPerfectFrameChime() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
            // Haptic vibration feedback for solo creator holding phone or feeling vibration
            triggerHapticFeedback(pattern = longArrayOf(0, 100, 80, 150))
        } catch (e: Exception) {
            Log.e("DirectorVoice", "Chime error", e)
        }
    }

    fun playCountdownBeep(number: Int) {
        try {
            if (number > 0) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
                triggerHapticFeedback(pattern = longArrayOf(0, 70))
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 300)
                triggerHapticFeedback(pattern = longArrayOf(0, 200))
            }
        } catch (e: Exception) {
            Log.e("DirectorVoice", "Tone error", e)
        }
    }

    private fun triggerHapticFeedback(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            // Ignore vibration failure if hardware unavailable
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.e("DirectorVoice", "Release error", e)
        }
    }
}
