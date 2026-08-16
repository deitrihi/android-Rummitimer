package com.deitrihi.rummitimer

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.deitrihi.rummitimer.ui.theme.RummitimerTheme
import kotlinx.coroutines.delay

enum class Screen { HOME, MENU, SETTINGS, SCORE_INPUT, RESULT, TWO_PLAYER_SETUP, TWO_PLAYER_TIMER, TWO_PLAYER_RESULT, POMODORO, STOPWATCH, GENERAL_TIMER }
enum class GameType { JANGGI, BADUK, CHESS }

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AdMobPolicy.initialize(this)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeHelper.getSelectedTheme(this)) }
            var keepScreenOn by remember { mutableStateOf(ThemeHelper.getKeepScreenOn(this)) }
            var alertSound by remember { mutableStateOf(AlertHelper.getSoundEnabled(this)) }
            var alertVibration by remember { mutableStateOf(AlertHelper.getVibrationEnabled(this)) }
            var alertFlash by remember { mutableStateOf(AlertHelper.getFlashEnabled(this)) }
            val darkTheme = when (themeMode) {
                ThemeHelper.THEME_DARK -> true
                ThemeHelper.THEME_LIGHT -> false
                else -> isSystemInDarkTheme()
            }
            SideEffect {
                if (keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
            RummitimerTheme(darkTheme = darkTheme) {
                RummitimerApp(
                    currentTheme = themeMode,
                    onThemeChange = { mode ->
                        ThemeHelper.setTheme(this, mode)
                        themeMode = mode
                    },
                    keepScreenOn = keepScreenOn,
                    onKeepScreenOnChange = { enabled ->
                        ThemeHelper.setKeepScreenOn(this, enabled)
                        keepScreenOn = enabled
                    },
                    alertSound = alertSound,
                    onAlertSoundChange = { v -> AlertHelper.setSoundEnabled(this, v); alertSound = v },
                    alertVibration = alertVibration,
                    onAlertVibrationChange = { v -> AlertHelper.setVibrationEnabled(this, v); alertVibration = v },
                    alertFlash = alertFlash,
                    onAlertFlashChange = { v -> AlertHelper.setFlashEnabled(this, v); alertFlash = v }
                )
            }
        }
    }
}

@Composable
fun RummitimerApp(
    currentTheme: String = ThemeHelper.THEME_SYSTEM,
    onThemeChange: (String) -> Unit = {},
    keepScreenOn: Boolean = false,
    onKeepScreenOnChange: (Boolean) -> Unit = {},
    alertSound: Boolean = true,
    onAlertSoundChange: (Boolean) -> Unit = {},
    alertVibration: Boolean = false,
    onAlertVibrationChange: (Boolean) -> Unit = {},
    alertFlash: Boolean = false,
    onAlertFlashChange: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val adManager = remember(activity) { activity?.let { InterstitialAdManager(it) } }
    LaunchedEffect(adManager) { adManager?.load() }

    val prefs = remember { context.getSharedPreferences("rummitimer_prefs", android.content.Context.MODE_PRIVATE) }
    val initialScreen = remember {
        when (prefs.getString("last_timer", "HOME")) {
            "JANGGI" -> Screen.TWO_PLAYER_SETUP
            "POMODORO" -> Screen.POMODORO
            "STOPWATCH" -> Screen.STOPWATCH
            "GENERAL_TIMER" -> Screen.GENERAL_TIMER
            else -> Screen.HOME
        }
    }
    var currentScreen by rememberSaveable { mutableStateOf(initialScreen) }
    var previousScreen by rememberSaveable { mutableStateOf(initialScreen) }
    var backPressedOnce by rememberSaveable { mutableStateOf(false) }
    var playerCount by rememberSaveable { mutableIntStateOf(2) }
    // penalties/scores는 구성 변경 시 초기화 허용 (rememberSaveable 불필요)
    var penalties by remember { mutableStateOf(List(4) { 0 }) }
    var scores by remember { mutableStateOf(List<Int?>(4) { null }) }
    var fruitIndices by remember { mutableStateOf(FruitHelper.getFruitIndices(context)) }

    // v3 2인 대국 타이머 상태
    var twoPlayerGameType by rememberSaveable { mutableStateOf(GameType.JANGGI) }
    var twoPlayerInitialTime by rememberSaveable { mutableIntStateOf(10 * 60) }
    var twoPlayerWinner by rememberSaveable { mutableIntStateOf(-1) }
    var twoPlayerP1Used by rememberSaveable { mutableIntStateOf(0) }
    var twoPlayerP2Used by rememberSaveable { mutableIntStateOf(0) }

    val timerScreens = setOf(
        Screen.HOME, Screen.POMODORO, Screen.STOPWATCH, Screen.GENERAL_TIMER, Screen.TWO_PLAYER_SETUP
    )

    LaunchedEffect(currentScreen) { backPressedOnce = false }

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(2000)
            backPressedOnce = false
        }
    }

    BackHandler(enabled = currentScreen in timerScreens) {
        if (backPressedOnce) {
            activity?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, context.getString(R.string.back_press_to_exit), Toast.LENGTH_SHORT).show()
        }
    }

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
            onMenuClick = { previousScreen = currentScreen; currentScreen = Screen.MENU },
            onEndGame = { currentScreen = Screen.SCORE_INPUT },
            onPenalty = { playerIndex ->
                penalties = penalties.toMutableList().also { it[playerIndex]++ }
            },
            onShowAd = { onAdDismissed ->
                adManager?.showAd(onAdDismissed) ?: onAdDismissed()
            }
        )
        Screen.MENU -> MenuScreen(
            onSelectRummicube = {
                prefs.edit().putString("last_timer", "HOME").apply()
                currentScreen = Screen.HOME
            },
            onSelectJanggi = {
                prefs.edit().putString("last_timer", "JANGGI").apply()
                twoPlayerGameType = GameType.JANGGI
                currentScreen = Screen.TWO_PLAYER_SETUP
            },
            onSelectPomodoro = {
                prefs.edit().putString("last_timer", "POMODORO").apply()
                currentScreen = Screen.POMODORO
            },
            onSelectStopwatch = {
                prefs.edit().putString("last_timer", "STOPWATCH").apply()
                currentScreen = Screen.STOPWATCH
            },
            onSelectGeneralTimer = {
                prefs.edit().putString("last_timer", "GENERAL_TIMER").apply()
                currentScreen = Screen.GENERAL_TIMER
            },
            onSelectSettings = { currentScreen = Screen.SETTINGS },
            onBack = { currentScreen = previousScreen }
        )
        Screen.POMODORO -> PomodoroScreen(
            onMenuClick = { previousScreen = currentScreen; currentScreen = Screen.MENU }
        )
        Screen.STOPWATCH -> StopwatchScreen(
            onMenuClick = { previousScreen = currentScreen; currentScreen = Screen.MENU }
        )
        Screen.GENERAL_TIMER -> GeneralTimerScreen(
            onMenuClick = { previousScreen = currentScreen; currentScreen = Screen.MENU }
        )
        Screen.TWO_PLAYER_SETUP -> TwoPlayerSetupScreen(
            gameType = twoPlayerGameType,
            onStart = { time ->
                twoPlayerInitialTime = time
                currentScreen = Screen.TWO_PLAYER_TIMER
            },
            onMenuClick = { previousScreen = currentScreen; currentScreen = Screen.MENU }
        )
        Screen.TWO_PLAYER_TIMER -> TwoPlayerTimerScreen(
            gameType = twoPlayerGameType,
            initialTimeSeconds = twoPlayerInitialTime,
            onGameEnd = { w, p1, p2 ->
                twoPlayerWinner = w
                twoPlayerP1Used = p1
                twoPlayerP2Used = p2
                currentScreen = Screen.TWO_PLAYER_RESULT
            },
            onBack = {
                adManager?.showAd { currentScreen = Screen.TWO_PLAYER_SETUP }
                    ?: run { currentScreen = Screen.TWO_PLAYER_SETUP }
            }
        )
        Screen.TWO_PLAYER_RESULT -> TwoPlayerResultScreen(
            winner = twoPlayerWinner,
            p1UsedSeconds = twoPlayerP1Used,
            p2UsedSeconds = twoPlayerP2Used,
            onRestart = {
                adManager?.showAd {
                    twoPlayerWinner = -1
                    currentScreen = Screen.TWO_PLAYER_TIMER
                } ?: run {
                    twoPlayerWinner = -1
                    currentScreen = Screen.TWO_PLAYER_TIMER
                }
            },
            onChangeSettings = {
                adManager?.showAd {
                    twoPlayerWinner = -1
                    currentScreen = Screen.TWO_PLAYER_SETUP
                } ?: run {
                    twoPlayerWinner = -1
                    currentScreen = Screen.TWO_PLAYER_SETUP
                }
            }
        )
        Screen.SETTINGS -> SettingsScreen(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            keepScreenOn = keepScreenOn,
            onKeepScreenOnChange = onKeepScreenOnChange,
            alertSound = alertSound,
            onAlertSoundChange = onAlertSoundChange,
            alertVibration = alertVibration,
            onAlertVibrationChange = onAlertVibrationChange,
            alertFlash = alertFlash,
            onAlertFlashChange = onAlertFlashChange,
            fruitIndices = fruitIndices,
            onFruitChange = { playerIndex, fruitIndex ->
                FruitHelper.setFruitIndex(context, playerIndex, fruitIndex)
                fruitIndices = FruitHelper.getFruitIndices(context)
            },
            onBack = { currentScreen = previousScreen }
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
