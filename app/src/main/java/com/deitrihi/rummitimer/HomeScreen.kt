// 홈 화면: 타이머 UI 및 플레이어 턴 관리
package com.deitrihi.rummitimer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val TURN_DURATION_OPTIONS = listOf(30, 60, 90, 120)
private val PLAYER_COUNT_OPTIONS = listOf(2, 3, 4)
private const val WARNING_THRESHOLD = 10

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
        if (timerStarted) {
            // 플레이 중: 버튼을 맨 위/아래에 고정, 양방향 타이머를 가운데 배치
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // 상단 버튼: 맞은편 플레이어용으로 180도 뒤집음
                ControlButtons(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .rotate(180f),
                    isRunning = isRunning,
                    isTimeUp = isTimeUp,
                    onStartPause = { startPause() },
                    onNext = { nextPlayer() },
                    onReset = onReset
                )

                // 중앙: 인디케이터 + 양방향 타이머 + 인디케이터
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
                        isTimeUp = isTimeUp
                    )
                    PlayerIndicators(
                        playerCount = playerCount,
                        currentPlayerIndex = currentPlayerIndex
                    )
                }

                // 하단 버튼: 현재 플레이어용 정상 방향
                ControlButtons(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    isRunning = isRunning,
                    isTimeUp = isTimeUp,
                    onStartPause = { startPause() },
                    onNext = { nextPlayer() },
                    onReset = onReset
                )
            }
        } else {
            // 초기 상태: 설정 영역 표시
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SettingsSection(
                    playerCount = playerCount,
                    turnDuration = turnDuration,
                    isRunning = isRunning,
                    onPlayerCountChange = { count ->
                        playerCount = count
                        if (currentPlayerIndex >= count) currentPlayerIndex = 0
                        resetTimer()
                    },
                    onTurnDurationChange = { duration ->
                        turnDuration = duration
                        resetTimer()
                        timeRemaining = duration
                    }
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PlayerTurnLabel(
                        currentPlayerIndex = currentPlayerIndex,
                        isTimeUp = isTimeUp
                    )
                    TimerDisplay(
                        timeRemaining = timeRemaining,
                        turnDuration = turnDuration,
                        isTimeUp = isTimeUp
                    )
                }

                ControlButtons(
                    isRunning = isRunning,
                    isTimeUp = isTimeUp,
                    onStartPause = { startPause() },
                    onNext = { nextPlayer() },
                    onReset = onReset
                )

                PlayerIndicators(
                    playerCount = playerCount,
                    currentPlayerIndex = currentPlayerIndex
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    playerCount: Int,
    turnDuration: Int,
    isRunning: Boolean,
    onPlayerCountChange: (Int) -> Unit,
    onTurnDurationChange: (Int) -> Unit
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
private fun PlayerTurnLabel(
    currentPlayerIndex: Int,
    isTimeUp: Boolean
) {
    Text(
        text = stringResource(R.string.player_turn_label, currentPlayerIndex + 1),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = if (isTimeUp) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurface
    )
}

// 초기 상태에서 사용하는 단방향 타이머 원
@Composable
private fun TimerDisplay(
    timeRemaining: Int,
    turnDuration: Int,
    isTimeUp: Boolean
) {
    val isWarning = timeRemaining <= WARNING_THRESHOLD && !isTimeUp
    val progress = if (turnDuration > 0) timeRemaining.toFloat() / turnDuration.toFloat() else 0f

    val progressColor by animateColorAsState(
        targetValue = when {
            isTimeUp -> MaterialTheme.colorScheme.error
            isWarning -> Color(0xFFFF6B35)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300),
        label = "progressColor"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "progress"
    )

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(300.dp),
            color = progressColor,
            strokeWidth = 20.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = timeRemaining / 60
            val seconds = timeRemaining % 60
            Text(
                text = if (isTimeUp) stringResource(R.string.time_up) else "%02d:%02d".format(minutes, seconds),
                fontSize = if (isTimeUp) 28.sp else 48.sp,
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

// 플레이 중에 사용하는 양방향 타이머 원 (상단: 180도 뒤집힌 시간, 하단: 정상 시간)
@Composable
private fun SplitTimerCircle(
    currentPlayerIndex: Int,
    timeRemaining: Int,
    turnDuration: Int,
    isTimeUp: Boolean
) {
    val isWarning = timeRemaining <= WARNING_THRESHOLD && !isTimeUp
    val progress = if (turnDuration > 0) timeRemaining.toFloat() / turnDuration.toFloat() else 0f

    val progressColor by animateColorAsState(
        targetValue = when {
            isTimeUp -> MaterialTheme.colorScheme.error
            isWarning -> Color(0xFFFF6B35)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300),
        label = "progressColor"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "progress"
    )

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeText = if (isTimeUp) stringResource(R.string.time_up) else "%02d:%02d".format(minutes, seconds)

    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 20.dp
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(300.dp),
            color = progressColor,
            strokeWidth = 20.dp
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 맞은편 플레이어가 읽을 수 있도록 180도 뒤집힌 시간
            Text(
                text = timeText,
                modifier = Modifier.rotate(180f),
                fontSize = if (isTimeUp) 24.sp else 42.sp,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )

            // 현재 플레이어 턴 레이블
            Text(
                text = stringResource(R.string.player_turn_label, currentPlayerIndex + 1),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isTimeUp) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )

            // 현재 플레이어가 읽는 정상 방향 시간
            Text(
                text = timeText,
                fontSize = if (isTimeUp) 24.sp else 42.sp,
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f)
        ) {
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

        FilledTonalButton(
            onClick = onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.btn_next))
        }
    }
}

@Composable
private fun PlayerIndicators(
    playerCount: Int,
    currentPlayerIndex: Int,
    modifier: Modifier = Modifier
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
                animationSpec = tween(300),
                label = "playerColor_$index"
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
