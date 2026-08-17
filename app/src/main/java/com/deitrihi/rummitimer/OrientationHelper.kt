// 화면 방향(적응형·가로·세로) 설정 저장
package com.deitrihi.rummitimer

import android.content.Context

object OrientationHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_ORIENTATION = "orientation"

    const val ORIENTATION_ADAPTIVE = "adaptive"
    const val ORIENTATION_LANDSCAPE = "landscape"
    const val ORIENTATION_PORTRAIT = "portrait"

    fun getSelectedOrientation(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ORIENTATION, ORIENTATION_ADAPTIVE) ?: ORIENTATION_ADAPTIVE
    }

    fun setOrientation(context: Context, orientation: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ORIENTATION, orientation).apply()
    }
}
