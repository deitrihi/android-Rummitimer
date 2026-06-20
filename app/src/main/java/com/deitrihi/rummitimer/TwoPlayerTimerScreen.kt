// 2인 대국 타이머 화면 — 플레이어 영역 전체 탭으로 착수 처리
package com.deitrihi.rummitimer

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val TWO_PLAYER_WARNING_THRESHOLD = 10

@Composable
fun TwoPlayerTimerScreen(
    gameType: GameType,
    initialTimeSeconds: Int,
    onGameEnd: (winner: Int, p1UsedSeconds: Int, p2UsedSeconds: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var player1Time by rememberSaveable { mutableIntStateOf(initialTimeSeconds) }
    var player2Time by rememberSaveable { mutableIntStateOf(initialTimeSeconds) }
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
                player1Time = maxOf(0, player1Time - 1)
                if (player1Time == 0) { winner = 1; activePlayer = -1; break }
                if (player1Time <= TWO_PLAYER_WARNING_THRESHOLD) MetronomePlayer.tick(warning = true)
                else if (player1Time % 10 == 0) MetronomePlayer.tick(warning = false)
            } else {
                player2Time = maxOf(0, player2Time - 1)
                if (player2Time == 0) { winner = 0; activePlayer = -1; break }
                if (player2Time <= TWO_PLAYER_WARNING_THRESHOLD) MetronomePlayer.tick(warning = true)
                else if (player2Time % 10 == 0) MetronomePlayer.tick(warning = false)
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

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        // 가로 모드: P1 좌 / ControlStrip 중앙 / P2 우
        Row(modifier = modifier.fillMaxSize().safeDrawingPadding()) {
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = player1Time,
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
                    onGameEnd(winner, initialTimeSeconds - player1Time, initialTimeSeconds - player2Time)
                }
            )
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = player2Time,
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
                timeSeconds = player2Time,
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
                    onGameEnd(winner, initialTimeSeconds - player1Time, initialTimeSeconds - player2Time)
                }
            )
            // P1 영역 — 하단
            PlayerHalf(
                modifier = Modifier.weight(1f),
                rotation = 0f,
                timeSeconds = player1Time,
                playerIndex = 0,
                isActive = activePlayer == 0,
                isGameStarted = gameStarted,
                winner = winner,
                onTap = { handleMove(0) }
            )
        }
    }
}

@Composable
private fun PlayerHalf(
    timeSeconds: Int,
    playerIndex: Int,
    isActive: Boolean,
    isGameStarted: Boolean,
    winner: Int,
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

    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60

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
                Text(
                    text = "%02d:%02d".format(minutes, seconds),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isGameOver && !isWinner) MaterialTheme.colorScheme.error
                            else textColor
                )
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
