// 2인 대국 타이머(장기·바둑·체스) 직전 설정값 저장/로드
package com.deitrihi.rummitimer

import android.content.Context

object TwoPlayerSetupHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_JANGGI_MINUTES = "janggi_minutes"
    private const val KEY_BADUK_MAIN_MINUTES = "baduk_main_minutes"
    private const val KEY_BADUK_BYOYOMI_SECONDS = "baduk_byoyomi_seconds"
    private const val KEY_BADUK_BYOYOMI_PERIODS = "baduk_byoyomi_periods"
    private const val KEY_CHESS_MAIN_MINUTES = "chess_main_minutes"
    private const val KEY_CHESS_INCREMENT_SECONDS = "chess_increment_seconds"

    fun getJanggiMinutes(context: Context): Int = prefs(context).getInt(KEY_JANGGI_MINUTES, 10)
    fun setJanggiMinutes(context: Context, minutes: Int) =
        prefs(context).edit().putInt(KEY_JANGGI_MINUTES, minutes).apply()

    fun getBadukMainMinutes(context: Context): Int = prefs(context).getInt(KEY_BADUK_MAIN_MINUTES, 30)
    fun setBadukMainMinutes(context: Context, minutes: Int) =
        prefs(context).edit().putInt(KEY_BADUK_MAIN_MINUTES, minutes).apply()

    fun getBadukByoyomiSeconds(context: Context): Int = prefs(context).getInt(KEY_BADUK_BYOYOMI_SECONDS, 30)
    fun setBadukByoyomiSeconds(context: Context, seconds: Int) =
        prefs(context).edit().putInt(KEY_BADUK_BYOYOMI_SECONDS, seconds).apply()

    fun getBadukByoyomiPeriods(context: Context): Int = prefs(context).getInt(KEY_BADUK_BYOYOMI_PERIODS, 3)
    fun setBadukByoyomiPeriods(context: Context, periods: Int) =
        prefs(context).edit().putInt(KEY_BADUK_BYOYOMI_PERIODS, periods).apply()

    fun getChessMainMinutes(context: Context): Int = prefs(context).getInt(KEY_CHESS_MAIN_MINUTES, 5)
    fun setChessMainMinutes(context: Context, minutes: Int) =
        prefs(context).edit().putInt(KEY_CHESS_MAIN_MINUTES, minutes).apply()

    fun getChessIncrementSeconds(context: Context): Int = prefs(context).getInt(KEY_CHESS_INCREMENT_SECONDS, 3)
    fun setChessIncrementSeconds(context: Context, seconds: Int) =
        prefs(context).edit().putInt(KEY_CHESS_INCREMENT_SECONDS, seconds).apply()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
