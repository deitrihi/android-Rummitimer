package com.deitrihi.rummitimer

import android.media.AudioManager
import android.media.ToneGenerator

object MetronomePlayer {

    fun tick(warning: Boolean = false) {
        Thread {
            runCatching {
                val generator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
                try {
                    val toneType = if (warning) ToneGenerator.TONE_DTMF_D else ToneGenerator.TONE_PROP_BEEP
                    val durationMs = if (warning) 60 else 100
                    generator.startTone(toneType, durationMs)
                    Thread.sleep(durationMs.toLong() + 80)
                } finally {
                    generator.release()
                }
            }
        }.start()
    }
}
