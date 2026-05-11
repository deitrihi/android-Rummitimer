// 홈 화면: 가로/세로/태블릿 레이아웃을 분리하여 처리하는 타이머 UI
package com.deitrihi.rummitimer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val TURN_DURATION_OPTIONS = listOf(30, 60, 90, 120)
private val PLAYER_COUNT_OPTIONS = listOf(2, 3, 4)
private const val WARNING_THRESHOLD = 10

private enum class LayoutType { PORTRAIT, LANDSCAPE }

@Composable
private fun currentLayoutType(): LayoutType {
    val config = LocalConfiguration.current
    return if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) LayoutType.LANDSCAPE
    else LayoutType.PORTRAIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var playerCount by rememberSaveable { mutableIntStateOf(2) }
    var turnDuration by rememberSaveable { mutableIntStateOf(60) }
    var currentPlayerIndex by rememberSaveable { mutableIntStateOf(0) }
    var timeRemaining by rememberSaveable { mutableIntStateOf(60) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var isTimeUp by rememberSaveable { mutableStateOf(false) }
    var autoStartTrigger by rememberSaveable { mutableIntStateOf(0) }
    var timerStarted by rememberSaveable { mutableStateOf(false) }

    val activity = LocalContext.current.findActivity()
    val adManager = remember(activity) { activity?.let { InterstitialAdManager(it) } }

    LaunchedEffect(adManager) {
        adManager?.load()
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeRemaining > 0) {
                delay(1000L)
                timeRemaining--
                if (timeRemaining > 0) {
                    val isWarning = timeRemaining <= WARNING_THRESHOLD
                    if (isWarning || timeRemaining % 10 == 0) {
                        MetronomePlayer.tick(warning = isWarning)
                    }
                }
            }
            isRunning = false
            isTimeUp = true
        }
    }

    LaunchedEffect(autoStartTrigger) {
        if (autoStartTrigger > 0) {
            delay(1000L)
            isRunning = true
        }
    }

    fun resetTimer() {
        isRunning = false
        isTimeUp = false
        timeRemaining = turnDuration
        timerStarted = false
    }

    fun startPause() {
        if (!isRunning) timerStarted = true
        isRunning = !isRunning
    }

    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerCount
        isRunning = false
        isTimeUp = false
        timeRemaining = turnDuration
        autoStartTrigger++
    }

    val onReset = { adManager?.showAd { resetTimer() } ?: resetTimer() }
    val onPlayerCountChange: (Int) -> Unit = { count ->
        playerCount = count
        if (currentPlayerIndex >= count) currentPlayerIndex = 0
        resetTimer()
    }
    val onTurnDurationChange: (Int) -> Unit = { duration ->
        turnDuration = duration
        resetTimer()
        timeRemaining = duration
    }

    val layoutType = currentLayoutType()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.menu_open)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (layoutType) {
            LayoutType.PORTRAIT -> PortraitContent(
                innerPadding = innerPadding,
                playerCount = playerCount,
                turnDuration = turnDuration,
                currentPlayerIndex = currentPlayerIndex,
                timeRemaining = timeRemaining,
                isRunning = isRunning,
                isTimeUp = isTimeUp,
                timerStarted = timerStarted,
                onStartPause = ::startPause,
                onNext = ::nextPlayer,
                onReset = onReset,
                onPlayerCountChange = onPlayerCountChange,
                onTurnDurationChange = onTurnDurationChange,
            )
            LayoutType.LANDSCAPE -> LandscapeContent(
                innerPadding = innerPadding,
                playerCount = playerCount,
                turnDuration = turnDuration,
                currentPlayerIndex = currentPlayerIndex,
                timeRemaining = timeRemaining,
                isRunning = isRunning,
                isTimeUp = isTimeUp,
                timerStarted = timerStarted,
                onStartPause = ::startPause,
                onNext = ::nextPlayer,
                onReset = onReset,
                onPlayerCountChange = onPlayerCountChange,
                onTurnDurationChange = onTurnDurationChange,
            )
        }
    }
}

// ── 세로 레이아웃 (Phone Portrait) ────────────────────────────────

@Composable
private fun PortraitContent(
    innerPadding: PaddingValues,
    playerCount: Int,
    turnDuration: Int,
    currentPlayerIndex: Int,
    timeRemaining: Int,
    isRunning: Boolean,
    isTimeUp: Boolean,
    timerStarted: Boolean,
    onStartPause: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit,
    onPlayerCountChange: (Int) -> Unit,
    onTurnDurationChange: (Int) -> Unit,
) {
    if (timerStarted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            ControlButtons(
                modifier = Modifier.align(Alignment.TopCenter).rotate(180f),
                isRunning = isRunning, isTimeUp = isTimeUp,
                onStartPause = onStartPause, onNext = onNext, onReset = onReset
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerIndicators(
                    playerCount = playerCount,
                    currentPlayerIndex = currentPlayerIndex,
                    modifier = Modifier.rotate(180f)
                )
                SplitTimerCircle(
                    currentPlayerIndex = currentPlayerIndex,
                    timeRemaining = timeRemaining,
                    turnDuration = turnDuration,
                    isTimeUp = isTimeUp,
                    size = 300.dp
                )
                PlayerIndicators(playerCount = playerCount, currentPlayerIndex = currentPlayerIndex)
            }
            ControlButtons(
                modifier = Modifier.align(Alignment.BottomCenter),
                isRunning = isRunning, isTimeUp = isTimeUp,
                onStartPause = onStartPause, onNext = onNext, onReset = onReset
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SettingsSection(
                playerCount = playerCount, turnDuration = turnDuration,
                isRunning = isRunning,
                onPlayerCountChange = onPlayerCountChange,
                onTurnDurationChange = onTurnDurationChange
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PlayerTurnLabel(currentPlayerIndex = currentPlayerIndex, isTimeUp = isTimeUp)
                TimerDisplay(
                    timeRemaining = timeRemaining, turnDuration = turnDuration,
                    isTimeUp = isTimeUp, size = 300.dp
                )
            }
            ControlButtons(
                isRunning = isRunning, isTimeUp = isTimeUp,
                onStartPause = onStartPause, onNext = onNext, onReset = onReset
            )
            PlayerIndicators(playerCount = playerCount, currentPlayerIndex = currentPlayerIndex)
        }
    }
}

// ── 가로 레이아웃 (Phone Landscape) ──────────────────────────────

@Composable
private fun LandscapeContent(
    innerPadding: PaddingValues,
    playerCount: Int,
    turnDuration: Int,
    currentPlayerIndex: Int,
    timeRemaining: Int,
    isRunning: Boolean,
    isTimeUp: Boolean,
    timerStarted: Boolean,
    onStartPause: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit,
    onPlayerCountChange: (Int) -> Unit,
    onTurnDurationChange: (Int) -> Unit,
) {
    if (timerStarted) {
        // 게임 중: 왼쪽=상대방 버튼(180도 회전), 중앙=인디케이터+타이머, 오른쪽=현재 플레이어 버튼
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                VerticalControlButtons(
                    isRunning = isRunning, isTimeUp = isTimeUp,
                    onStartPause = onStartPause, onNext = onNext, onReset = onReset
                )
            }

            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PlayerIndicators(playerCount = playerCount, currentPlayerIndex = currentPlayerIndex)
                Spacer(modifier = Modifier.height(8.dp))
                TimerDisplay(
                    timeRemaining = timeRemaining,
                    turnDuration = turnDuration,
                    isTimeUp = isTimeUp,
                    size = 200.dp,
                    strokeWidth = 13.dp,
                    timerFontSize = 32.sp,
                    timeUpFontSize = 19.sp,
                    playerLabel = stringResource(R.string.player_turn_label, currentPlayerIndex + 1),
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                VerticalControlButtons(
                    isRunning = isRunning, isTimeUp = isTimeUp,
                    onStartPause = onStartPause, onNext = onNext, onReset = onReset
                )
            }
        }
    } else {
        // 초기 상태: 왼쪽=설정, 오른쪽=타이머+컨트롤
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SettingsSection(
                    playerCount = playerCount, turnDuration = turnDuration,
                    isRunning = isRunning,
                    onPlayerCountChange = onPlayerCountChange,
                    onTurnDurationChange = onTurnDurationChange
                )
            }

            VerticalDivider()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerTurnLabel(currentPlayerIndex = currentPlayerIndex, isTimeUp = isTimeUp)
                TimerDisplay(
                    timeRemaining = timeRemaining, turnDuration = turnDuration,
                    isTimeUp = isTimeUp, size = 200.dp,
                    strokeWidth = 13.dp, timerFontSize = 32.sp, timeUpFontSize = 19.sp,
                )
                ControlButtons(
                    isRunning = isRunning, isTimeUp = isTimeUp,
                    onStartPause = onStartPause, onNext = onNext, onReset = onReset
                )
                PlayerIndicators(playerCount = playerCount, currentPlayerIndex = currentPlayerIndex)
            }
        }
    }
}

// ── 공통 UI 컴포넌트 ─────────────────────────────────────────────

@Composable
private fun SettingsSection(
    playerCount: Int,
    turnDuration: Int,
    isRunning: Boolean,
    onPlayerCountChange: (Int) -> Unit,
    onTurnDurationChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.player_count_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PLAYER_COUNT_OPTIONS.forEach { count ->
                FilterChip(
                    selected = playerCount == count,
                    onClick = { if (!isRunning) onPlayerCountChange(count) },
                    label = { Text(stringResource(R.string.player_count_format, count)) },
                    enabled = !isRunning
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.turn_duration_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TURN_DURATION_OPTIONS.forEach { duration ->
                FilterChip(
                    selected = turnDuration == duration,
                    onClick = { if (!isRunning) onTurnDurationChange(duration) },
                    label = { Text(stringResource(R.string.turn_duration_format, duration)) },
                    enabled = !isRunning
                )
            }
        }
    }
}

@Composable
private fun PlayerTurnLabel(currentPlayerIndex: Int, isTimeUp: Boolean) {
    Text(
        text = stringResource(R.string.player_turn_label, currentPlayerIndex + 1),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = if (isTimeUp) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TimerDisplay(
    timeRemaining: Int,
    turnDuration: Int,
    isTimeUp: Boolean,
    size: Dp = 300.dp,
    strokeWidth: Dp = 20.dp,
    timerFontSize: TextUnit = 48.sp,
    timeUpFontSize: TextUnit = 28.sp,
    playerLabel: String? = null,
) {
    val isWarning = timeRemaining <= WARNING_THRESHOLD && !isTimeUp
    val progress = if (turnDuration > 0) timeRemaining.toFloat() / turnDuration.toFloat() else 0f

    val progressColor by animateColorAsState(
        targetValue = when {
            isTimeUp -> MaterialTheme.colorScheme.error
            isWarning -> Color(0xFFFF6B35)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300), label = "progressColor"
    )
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(800), label = "progress"
    )

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f }, modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.surfaceVariant, strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = { animatedProgress }, modifier = Modifier.size(size),
            color = progressColor, strokeWidth = strokeWidth
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            if (playerLabel != null) {
                Text(
                    text = playerLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isTimeUp) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (isTimeUp) stringResource(R.string.time_up) else "%02d:%02d".format(minutes, seconds),
                fontSize = if (isTimeUp) timeUpFontSize else timerFontSize,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
            if (isWarning && !isTimeUp) {
                Text(
                    text = stringResource(R.string.hurry_up),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6B35)
                )
            }
        }
    }
}

@Composable
private fun SplitTimerCircle(
    currentPlayerIndex: Int,
    timeRemaining: Int,
    turnDuration: Int,
    isTimeUp: Boolean,
    size: Dp = 300.dp,
    strokeWidth: Dp = 20.dp,
    timerFontSize: TextUnit = 42.sp,
    timeUpFontSize: TextUnit = 24.sp,
) {
    val isWarning = timeRemaining <= WARNING_THRESHOLD && !isTimeUp
    val progress = if (turnDuration > 0) timeRemaining.toFloat() / turnDuration.toFloat() else 0f

    val progressColor by animateColorAsState(
        targetValue = when {
            isTimeUp -> MaterialTheme.colorScheme.error
            isWarning -> Color(0xFFFF6B35)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300), label = "progressColor"
    )
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(800), label = "progress"
    )

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeText = if (isTimeUp) stringResource(R.string.time_up) else "%02d:%02d".format(minutes, seconds)

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f }, modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.surfaceVariant, strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = { animatedProgress }, modifier = Modifier.size(size),
            color = progressColor, strokeWidth = strokeWidth
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = timeText,
                modifier = Modifier.rotate(180f),
                fontSize = if (isTimeUp) timeUpFontSize else timerFontSize,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
            Text(
                text = stringResource(R.string.player_turn_label, currentPlayerIndex + 1),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isTimeUp) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = timeText,
                fontSize = if (isTimeUp) timeUpFontSize else timerFontSize,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
            if (isWarning && !isTimeUp) {
                Text(
                    text = stringResource(R.string.hurry_up),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFF6B35)
                )
            }
        }
    }
}

@Composable
private fun ControlButtons(
    isRunning: Boolean,
    isTimeUp: Boolean,
    onStartPause: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.btn_reset))
        }
        Button(
            onClick = onStartPause,
            modifier = Modifier.weight(1.4f),
            enabled = !isTimeUp,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isRunning) stringResource(R.string.btn_pause) else stringResource(R.string.btn_start),
                fontWeight = FontWeight.Bold
            )
        }
        FilledTonalButton(onClick = onNext, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.btn_next))
        }
    }
}

@Composable
private fun VerticalControlButtons(
    isRunning: Boolean,
    isTimeUp: Boolean,
    onStartPause: () -> Unit,
    onNext: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalButton(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.btn_next))
        }
        Button(
            onClick = onStartPause,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTimeUp,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isRunning) stringResource(R.string.btn_pause) else stringResource(R.string.btn_start),
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.btn_reset))
        }
    }
}

@Composable
private fun PlayerIndicators(
    playerCount: Int,
    currentPlayerIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(playerCount) { index ->
            val isActive = index == currentPlayerIndex
            val color by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(300), label = "playerColor_$index"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = color,
                    modifier = Modifier.size(if (isActive) 16.dp else 12.dp)
                ) {}
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "P${index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
