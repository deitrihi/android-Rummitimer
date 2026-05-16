// 플레이어별 과일 아이콘 인덱스를 SharedPreferences에 저장/조회하는 헬퍼
package com.deitrihi.rummitimer

import android.content.Context

val FRUIT_EMOJIS = listOf("🍎", "🍌", "🍇", "🍓", "🍉", "🍊", "🍑", "🍒")

object FruitHelper {
    private const val PREFS_NAME = "rummitimer_settings"
    private const val KEY_FRUIT_PREFIX = "fruit_p"

    fun getFruitIndices(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return List(4) { i -> prefs.getInt("$KEY_FRUIT_PREFIX${i + 1}", i) }
    }

    fun setFruitIndex(context: Context, playerIndex: Int, fruitIndex: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt("$KEY_FRUIT_PREFIX${playerIndex + 1}", fruitIndex).apply()
    }
}
