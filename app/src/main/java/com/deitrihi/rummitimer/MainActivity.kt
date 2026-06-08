package com.deitrihi.rummitimer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.deitrihi.rummitimer.ui.theme.RummitimerTheme

enum class Screen { HOME, MENU, SETTINGS, SCORE_INPUT, RESULT }

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeHelper.getSelectedTheme(this)) }
            val darkTheme = when (themeMode) {
                ThemeHelper.THEME_DARK -> true
                ThemeHelper.THEME_LIGHT -> false
                else -> isSystemInDarkTheme()
            }
            RummitimerTheme(darkTheme = darkTheme) {
                RummitimerApp(
                    currentTheme = themeMode,
                    onThemeChange = { mode ->
                        ThemeHelper.setTheme(this, mode)
                        themeMode = mode
                    }
                )
            }
        }
    }
}

@Composable
fun RummitimerApp(
    currentTheme: String = ThemeHelper.THEME_SYSTEM,
    onThemeChange: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val adManager = remember(activity) { activity?.let { InterstitialAdManager(it) } }
    LaunchedEffect(adManager) { adManager?.load() }

    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var playerCount by rememberSaveable { mutableIntStateOf(2) }
    // penalties/scores는 구성 변경 시 초기화 허용 (rememberSaveable 불필요)
    var penalties by remember { mutableStateOf(List(4) { 0 }) }
    var scores by remember { mutableStateOf(List<Int?>(4) { null }) }
    var fruitIndices by remember { mutableStateOf(FruitHelper.getFruitIndices(context)) }

    fun resetGame() {
        penalties = List(4) { 0 }
        scores = List(4) { null }
        currentScreen = Screen.HOME
    }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            playerCount = playerCount,
            onPlayerCountChange = { playerCount = it },
            fruitIndices = fruitIndices,
            penalties = penalties,
            onMenuClick = { currentScreen = Screen.MENU },
            onEndGame = { currentScreen = Screen.SCORE_INPUT },
            onPenalty = { playerIndex ->
                penalties = penalties.toMutableList().also { it[playerIndex]++ }
            }
        )
        Screen.MENU -> MenuScreen(
            onSelectRummicube = { currentScreen = Screen.HOME },
            onSelectSettings = { currentScreen = Screen.SETTINGS },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.SETTINGS -> SettingsScreen(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            fruitIndices = fruitIndices,
            onFruitChange = { playerIndex, fruitIndex ->
                FruitHelper.setFruitIndex(context, playerIndex, fruitIndex)
                fruitIndices = FruitHelper.getFruitIndices(context)
            },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.SCORE_INPUT -> ScoreInputScreen(
            playerCount = playerCount,
            fruitIndices = fruitIndices,
            onComplete = { enteredScores ->
                scores = enteredScores
                currentScreen = Screen.RESULT
            }
        )
        Screen.RESULT -> ResultScreen(
            playerCount = playerCount,
            fruitIndices = fruitIndices,
            scores = scores,
            penalties = penalties,
            onComplete = {
                adManager?.showAd { resetGame() } ?: resetGame()
            }
        )
    }
}
