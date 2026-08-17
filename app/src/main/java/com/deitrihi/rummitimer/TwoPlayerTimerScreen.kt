// 2인 대국 타이머 화면 — 플레이어 영역 전체 탭으로 착수 처리
package com.deitrihi.rummitimer

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val TWO_PLAYER_WARNING_THRESHOLD = 10

// 한 플레이어의 시계를 1초 진행시킨다. 메인 시간 소진 후 초읽기(byoyomi)가 있으면 진입/소모/리셋을 처리한다.
private data class ClockTickResult(
    val mainTime: Int,
    val inByoyomi: Boolean,
    val byoyomiTime: Int,
    val byoyomiPeriodsLeft: Int,
    val lost: Boolean,
)

private fun tickClock(
    mainTime: Int,
    inByoyomi: Boolean,
    byoyomiTime: Int,
    byoyomiPeriodsLeft: Int,
    byoyomiSeconds: Int,
): ClockTickResult {
    if (!inByoyomi) {
        val newMain = mainTime - 1
        if (newMain > 0) return ClockTickResult(newMain, false, byoyomiTime, byoyomiPeriodsLeft, lost = false)
        return if (byoyomiPeriodsLeft > 0) {
            ClockTickResult(0, true, byoyomiSeconds, byoyomiPeriodsLeft, lost = false)
        } else {
            ClockTickResult(0, false, byoyomiTime, byoyomiPeriodsLeft, lost = true)
        }
    }
    val newByoyomi = byoyomiTime - 1
    if (newByoyomi > 0) return ClockTickResult(mainTime, true, newByoyomi, byoyomiPeriodsLeft, lost = false)
    val periodsLeft = byoyomiPeriodsLeft - 1
    return if (periodsLeft > 0) {
        ClockTickResult(mainTime, true, byoyomiSeconds, periodsLeft, lost = false)
    } else {
        ClockTickResult(mainTime, true, 0, 0, lost = true)
    }
}

@Composable
fun TwoPlayerTimerScreen(
    gameType: GameType,
    initialTimeSeconds: Int,
    byoyomiSeconds: Int = 0,
    byoyomiPeriods: Int = 0,
    incrementSeconds: Int = 0,
    onGameEnd: (winner: Int, p1UsedSeconds: Int, p2UsedSeconds: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var flashVisible by remember { mutableStateOf(false) }

    fun triggerFlash() = scope.launch {
        repeat(3) {
            flashVisible = true
            delay(200)
            flashVisible = false
            delay(200)
        }
    }

    var player1Time by rememberSaveable { mutableIntStateOf(initialTimeSeconds) }
    var player2Time by rememberSaveable { mutableIntStateOf(initialTimeSeconds) }
    var player1InByoyomi by rememberSaveable { mutableStateOf(false) }
    var player2InByoyomi by rememberSaveable { mutableStateOf(false) }
    var player1ByoyomiTime by rememberSaveable { mutableIntStateOf(byoyomiSeconds) }
    var player2ByoyomiTime by rememberSaveable { mutableIntStateOf(byoyomiSeconds) }
    var player1ByoyomiPeriodsLeft by rememberSaveable { mutableIntStateOf(byoyomiPeriods) }
    var player2ByoyomiPeriodsLeft by rememberSaveable { mutableIntStateOf(byoyomiPeriods) }
    // -1=미시작/일시정지, 0=P1 활성, 1=P2 활성
    var activePlayer by rememberSaveable { mutableIntStateOf(-1) }
    var lastActivePlayer by rememberSaveable { mutableIntStateOf(0) }
    var gameStarted by rememberSaveable { mutableStateOf(false) }
    var winner by rememberSaveable { mutableIntStateOf(-1) }

    // 단일 루프로 race condition 없이 매초 현재 activePlayer를 읽어 차감
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            val active = activePlayer
            if (active < 0 || winner >= 0) continue
            if (active == 0) {
                val result = tickClock(player1Time, player1InByoyomi, player1ByoyomiTime, player1ByoyomiPeriodsLeft, byoyomiSeconds)
                player1Time = result.mainTime
                player1InByoyomi = result.inByoyomi
                player1ByoyomiTime = result.byoyomiTime
                player1ByoyomiPeriodsLeft = result.byoyomiPeriodsLeft
                if (result.lost) {
                    winner = 1; activePlayer = -1
                    AlertHelper.triggerAlerts(context, warning = true) { triggerFlash() }
                    break
                }
                val remaining = if (result.inByoyomi) result.byoyomiTime else result.mainTime
                if (remaining <= TWO_PLAYER_WARNING_THRESHOLD) {
                    if (AlertHelper.getSoundEnabled(context)) MetronomePlayer.tick(warning = true)
                } else if (remaining % 10 == 0) MetronomePlayer.tick(warning = false)
            } else {
                val result = tickClock(player2Time, player2InByoyomi, player2ByoyomiTime, player2ByoyomiPeriodsLeft, byoyomiSeconds)
                player2Time = result.mainTime
                player2InByoyomi = result.inByoyomi
                player2ByoyomiTime = result.byoyomiTime
                player2ByoyomiPeriodsLeft = result.byoyomiPeriodsLeft
                if (result.lost) {
                    winner = 0; activePlayer = -1
                    AlertHelper.triggerAlerts(context, warning = true) { triggerFlash() }
                    break
                }
                val remaining = if (result.inByoyomi) result.byoyomiTime else result.mainTime
                if (remaining <= TWO_PLAYER_WARNING_THRESHOLD) {
                    if (AlertHelper.getSoundEnabled(context)) MetronomePlayer.tick(warning = true)
                } else if (remaining % 10 == 0) MetronomePlayer.tick(warning = false)
            }
        }
    }

    fun startGame() {
        gameStarted = true
        lastActivePlayer = 0
        activePlayer = 0
    }

    fun handleMove(playerIndex: Int) {
        if (activePlayer == playerIndex && winner < 0) {
            // 착수 완료: 초읽기 중이면 시간 내 착수이므로 리셋, 증가시간 모드면 자기 시간에 가산
            if (playerIndex == 0) {
                if (player1InByoyomi) player1ByoyomiTime = byoyomiSeconds
                if (incrementSeconds > 0) player1Time += incrementSeconds
            } else {
                if (player2InByoyomi) player2ByoyomiTime = byoyomiSeconds
                if (incrementSeconds > 0) player2Time += incrementSeconds
            }
            val next = 1 - playerIndex
            lastActivePlayer = next
            activePlayer = next
        }
    }

    fun togglePause() {
        if (!gameStarted || winner >= 0) return
        if (activePlayer >= 0) {
            lastActivePlayer = activePlayer
            activePlayer = -1
        } else {
            activePlayer = lastActivePlayer
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }
    val isGameInProgress = gameStarted && winner < 0

    BackHandler(enabled = !showExitDialog) {
        if (isGameInProgress) showExitDialog = true else onBack()
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.timer_exit_title)) },
            text = { Text(stringResource(R.string.timer_exit_message)) },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onBack() }) {
                    Text(stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val flashColor = MaterialTheme.colorScheme.error

    Box(modifier = modifier.fillMaxSize()) {
    if (isLandscape) {
        // 가로 모드: P1 좌 / ControlStrip 중앙 / P2 우
        Row(modifier = modifier.fillMaxSize().safeDrawingPadding()) {
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = if (player1InByoyomi) player1ByoyomiTime else player1Time,
                totalSeconds = if (player1InByoyomi) byoyomiSeconds else initialTimeSeconds,
                inByoyomi = player1InByoyomi,
                byoyomiPeriodsLeft = player1ByoyomiPeriodsLeft,
                playerIndex = 0,
                isActive = activePlayer == 0,
                isGameStarted = gameStarted,
                winner = winner,
                onTap = { handleMove(0) }
            )
            ControlStrip(
                isGameStarted = gameStarted,
                isPaused = activePlayer < 0 && gameStarted,
                isGameOver = winner >= 0,
                isVertical = true,
                onStart = ::startGame,
                onTogglePause = ::togglePause,
                onEnd = onBack,
                onSeeResult = {
                    onGameEnd(
                        winner,
                        maxOf(0, initialTimeSeconds - player1Time),
                        maxOf(0, initialTimeSeconds - player2Time)
                    )
                }
            )
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = if (player2InByoyomi) player2ByoyomiTime else player2Time,
                totalSeconds = if (player2InByoyomi) byoyomiSeconds else initialTimeSeconds,
                inByoyomi = player2InByoyomi,
                byoyomiPeriodsLeft = player2ByoyomiPeriodsLeft,
                playerIndex = 1,
                isActive = activePlayer == 1,
                isGameStarted = gameStarted,
                winner = winner,
                onTap = { handleMove(1) }
            )
        }
    } else {
        // 세로 모드: 기존 레이아웃
        Column(modifier = modifier.fillMaxSize().safeDrawingPadding()) {
            // P2 영역 — 상단, 180도 회전 (P2가 자신의 방향에서 읽음)
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 180f,
                timeSeconds = if (player2InByoyomi) player2ByoyomiTime else player2Time,
                totalSeconds = if (player2InByoyomi) byoyomiSeconds else initialTimeSeconds,
                inByoyomi = player2InByoyomi,
                byoyomiPeriodsLeft = player2ByoyomiPeriodsLeft,
                playerIndex = 1,
                isActive = activePlayer == 1,
                isGameStarted = gameStarted,
                winner = winner,
                onTap = { handleMove(1) }
            )
            // 중앙 컨트롤 스트립
            ControlStrip(
                isGameStarted = gameStarted,
                isPaused = activePlayer < 0 && gameStarted,
                isGameOver = winner >= 0,
                isVertical = false,
                onStart = ::startGame,
                onTogglePause = ::togglePause,
                onEnd = onBack,
                onSeeResult = {
                    onGameEnd(
                        winner,
                        maxOf(0, initialTimeSeconds - player1Time),
                        maxOf(0, initialTimeSeconds - player2Time)
                    )
                }
            )
            // P1 영역 — 하단
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = if (player1InByoyomi) player1ByoyomiTime else player1Time,
                totalSeconds = if (player1InByoyomi) byoyomiSeconds else initialTimeSeconds,
                inByoyomi = player1InByoyomi,
                byoyomiPeriodsLeft = player1ByoyomiPeriodsLeft,
                playerIndex = 0,
                isActive = activePlayer == 0,
                isGameStarted = gameStarted,
                winner = winner,
                onTap = { handleMove(0) }
            )
        }
    }
    if (flashVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(12.dp, flashColor, RectangleShape)
        )
    }
    }
}

@Composable
private fun PlayerHalf(
    timeSeconds: Int,
    totalSeconds: Int,
    playerIndex: Int,
    isActive: Boolean,
    isGameStarted: Boolean,
    winner: Int,
    inByoyomi: Boolean = false,
    byoyomiPeriodsLeft: Int = 0,
    rotation: Float = 0f,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isGameOver = winner >= 0
    val isWinner = winner == playerIndex

    val bgColor by animateColorAsState(
        targetValue = when {
            isGameOver && isWinner -> MaterialTheme.colorScheme.tertiaryContainer
            isGameOver -> MaterialTheme.colorScheme.errorContainer
            isActive -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "bg_p$playerIndex"
    )

    val textColor = when {
        isGameOver && isWinner -> MaterialTheme.colorScheme.onTertiaryContainer
        isGameOver -> MaterialTheme.colorScheme.onErrorContainer
        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }
    val ringColor = if (isGameOver && !isWinner) MaterialTheme.colorScheme.error else textColor

    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60
    // 남은 시간 비율 × 360°의 양수 sweepAngle → 링이 반시계 방향으로 줄어드는 효과
    val sweepAngle = if (totalSeconds > 0) timeSeconds.toFloat() / totalSeconds * 360f else 0f

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = isActive && isGameStarted && !isGameOver, onClick = onTap),
        color = bgColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Surface는 영역을 꽉 채우고, 텍스트 컨텐츠만 회전
            AutoFitContent {
            Column(
                modifier = Modifier.rotate(rotation),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "P${playerIndex + 1}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                if (inByoyomi && !isGameOver) {
                    Text(
                        text = stringResource(R.string.byoyomi_periods_remaining_format, byoyomiPeriodsLeft),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2f
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val strokeWidth = radius * 0.14f
                        val ringRadius = radius - strokeWidth / 2f

                        drawCircle(
                            color = ringColor.copy(alpha = 0.2f),
                            radius = ringRadius,
                            center = Offset(cx, cy),
                            style = Stroke(width = strokeWidth)
                        )
                        if (sweepAngle > 0f) {
                            drawArc(
                                color = ringColor,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(cx - ringRadius, cy - ringRadius),
                                size = Size(ringRadius * 2, ringRadius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        // 초읽기 예비 횟수: 바깥쪽(현재 진행 중) 링 안쪽에 남은 횟수만큼 정적인 링을 서로 맞닿을 만큼
                        // 촘촘히 겹쳐 그리되, 링끼리 구분되도록 얇은 간격만 남긴다. 횟수가 줄어들면 가장 안쪽 링부터 사라진다.
                        if (inByoyomi && !isGameOver) {
                            val reserveStroke = strokeWidth * 0.6f
                            val edgeGap = strokeWidth * 0.12f
                            val reserveCount = (byoyomiPeriodsLeft - 1).coerceAtLeast(0)
                            var innerEdge = ringRadius - strokeWidth / 2f
                            for (i in 1..reserveCount) {
                                val reserveRadius = innerEdge - edgeGap - reserveStroke / 2f
                                if (reserveRadius <= reserveStroke) break
                                drawCircle(
                                    color = ringColor.copy(alpha = 0.45f),
                                    radius = reserveRadius,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = reserveStroke)
                                )
                                innerEdge = reserveRadius - reserveStroke / 2f
                            }
                        }
                    }
                    Text(
                        text = "%02d:%02d".format(minutes, seconds),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGameOver && !isWinner) MaterialTheme.colorScheme.error
                                else textColor
                    )
                }
                when {
                    isGameOver && isWinner ->
                        Text(
                            text = "🏆 ${stringResource(R.string.game_winner)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    isGameOver && !isWinner ->
                        Text(
                            text = stringResource(R.string.time_up),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    isActive && isGameStarted ->
                        Text(
                            text = stringResource(R.string.tap_to_move),
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    !isActive && isGameStarted ->
                        Text(
                            text = stringResource(R.string.waiting),
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor
                        )
                }
            }
            }
        }
    }
}

@Composable
private fun ControlStrip(
    isGameStarted: Boolean,
    isPaused: Boolean,
    isGameOver: Boolean,
    isVertical: Boolean,
    onStart: () -> Unit,
    onTogglePause: () -> Unit,
    onEnd: () -> Unit,
    onSeeResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = if (isVertical) modifier.fillMaxHeight().width(128.dp)
                   else modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        if (isVertical) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    !isGameStarted -> Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.btn_start), fontWeight = FontWeight.Bold)
                    }
                    isGameOver -> Button(
                        onClick = onSeeResult,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AutoFitText(stringResource(R.string.btn_see_result))
                    }
                    else -> {
                        OutlinedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth()) {
                            AutoFitText(stringResource(R.string.btn_reset))
                        }
                        Button(
                            onClick = onTogglePause,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            AutoFitText(
                                if (isPaused) stringResource(R.string.btn_resume)
                                else stringResource(R.string.btn_pause)
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    !isGameStarted -> Button(
                        onClick = onStart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.btn_start), fontWeight = FontWeight.Bold)
                    }
                    isGameOver -> Button(
                        onClick = onSeeResult,
                        modifier = Modifier.weight(1f)
                    ) {
                        AutoFitText(stringResource(R.string.btn_see_result))
                    }
                    else -> {
                        OutlinedButton(onClick = onEnd, modifier = Modifier.weight(1f)) {
                            AutoFitText(stringResource(R.string.btn_reset))
                        }
                        Button(
                            onClick = onTogglePause,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            AutoFitText(
                                if (isPaused) stringResource(R.string.btn_resume)
                                else stringResource(R.string.btn_pause)
                            )
                        }
                    }
                }
            }
        }
    }
}
