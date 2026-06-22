package com.deitrihi.rummitimer

import android.content.Context

object ThemeHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_THEME = "theme"
    private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"

    const val THEME_SYSTEM = "system"
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"

    fun getSelectedTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun setTheme(context: Context, theme: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, theme).apply()
    }

    fun getKeepScreenOn(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_KEEP_SCREEN_ON, false)
    }

    fun setKeepScreenOn(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply()
    }
}
