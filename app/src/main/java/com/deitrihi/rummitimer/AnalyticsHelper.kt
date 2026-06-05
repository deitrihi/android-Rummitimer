// Firebase Analytics 이벤트 로깅 헬퍼
package com.deitrihi.rummitimer

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {
    const val MENU_OPEN = "btn_menu_open"
    const val PLAYER_COUNT = "btn_player_count"
    const val TURN_DURATION = "btn_turn_duration"
    const val TIMER_START = "btn_timer_start"
    const val TIMER_PAUSE = "btn_timer_pause"
    const val NEXT_PLAYER = "btn_next_player"
    const val END_GAME = "btn_end_game"
    const val SETTINGS_BACK = "btn_settings_back"
    const val LANGUAGE_CHANGE = "btn_language_change"
    const val THEME_CHANGE = "btn_theme_change"
    const val FRUIT_CHANGE = "btn_fruit_change"
    const val SCORE_SUBMIT = "btn_score_submit"
    const val GAME_RESET = "btn_game_reset"

    fun log(context: Context, event: String, vararg params: Pair<String, Any>) {
        val bundle = if (params.isEmpty()) null else Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Long -> putLong(key, value)
                    is Int -> putLong(key, value.toLong())
                    is Double -> putDouble(key, value)
                }
            }
        }
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }
}
