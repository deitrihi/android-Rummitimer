package com.deitrihi.rummitimer

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_LANGUAGE = "language"

    const val LANG_SYSTEM = "system"
    const val LANG_KOREAN = "ko"
    const val LANG_ENGLISH = "en"
    const val LANG_JAPANESE = "ja"
    const val LANG_GERMAN = "de"
    const val LANG_SPANISH = "es"
    const val LANG_DUTCH = "nl"

    fun getSelectedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
    }

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun applyLocale(context: Context): Context {
        val lang = getSelectedLanguage(context)
        if (lang == LANG_SYSTEM) return context

        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
