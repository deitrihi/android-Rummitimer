// 알림 방법(소리·진동·깜박임) 설정 저장 및 실행
package com.deitrihi.rummitimer

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AlertHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_SOUND = "alert_sound"
    private const val KEY_VIBRATION = "alert_vibration"
    private const val KEY_FLASH = "alert_flash"

    fun getSoundEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SOUND, true)

    fun getVibrationEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_VIBRATION, false)

    fun getFlashEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_FLASH, false)

    fun setSoundEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_SOUND, enabled).apply()

    fun setVibrationEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_VIBRATION, enabled).apply()

    fun setFlashEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_FLASH, enabled).apply()

    // 활성화된 알림을 모두 실행한다. onFlash는 깜박임이 활성화된 경우에만 호출된다.
    fun triggerAlerts(context: Context, warning: Boolean = true, onFlash: () -> Unit = {}) {
        if (getSoundEnabled(context)) MetronomePlayer.tick(warning = warning)
        if (getVibrationEnabled(context)) vibrate(context)
        if (getFlashEnabled(context)) onFlash()
    }

    fun vibrate(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v?.vibrate(500)
            }
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
