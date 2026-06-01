package com.aysimaerdem.catchpaw.ui.sound

import android.media.AudioManager
import android.media.ToneGenerator

class SoundManager {
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (_: Exception) { }
    }

    fun playCatch() = playTone(ToneGenerator.TONE_PROP_BEEP, 80)
    fun playBonusCatch() = playTone(ToneGenerator.TONE_PROP_BEEP2, 130)
    fun playBomb() = playTone(ToneGenerator.TONE_SUP_BUSY, 160)
    fun playPowerUp() = playTone(ToneGenerator.TONE_SUP_RADIO_ACK, 130)

    private fun playTone(tone: Int, durationMs: Int) {
        try { toneGen?.startTone(tone, durationMs) } catch (_: Exception) { }
    }

    fun release() {
        try { toneGen?.release() } catch (_: Exception) { }
        toneGen = null
    }
}
