// 뽀모도로 타이머 화면 — 집중·짧은휴식·긴휴식 사이클 관리
package com.deitrihi.rummitimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val FOCUS_SECONDS = 25 * 60
private const val SHORT_BREAK_SECONDS = 5 * 60
private const val LONG_BREAK_SECONDS = 15 * 60
private const val SESSIONS_BEFORE_LONG_BREAK = 4

private enum class PomodoroPhase { FOCUS, SHORT_BREAK, LONG_BREAK }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var phase by rememberSaveable { mutableStateOf(PomodoroPhase.FOCUS) }
    var timeLeft by rememberSaveable { mutableIntStateOf(FOCUS_SECONDS) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var completedFocusSessions by rememberSaveable { mutableIntStateOf(0) }

    val totalSeconds = when (phase) {
        PomodoroPhase.FOCUS -> FOCUS_SECONDS
        PomodoroPhase.SHORT_BREAK -> SHORT_BREAK_SECONDS
        PomodoroPhase.LONG_BREAK -> LONG_BREAK_SECONDS
    }

    fun advancePhase() {
        when (phase) {
            PomodoroPhase.FOCUS -> {
                val next = completedFocusSessions + 1
                completedFocusSessions = next % SESSIONS_BEFORE_LONG_BREAK
                if (next % SESSIONS_BEFORE_LONG_BREAK == 0) {
                    phase = PomodoroPhase.LONG_BREAK
                    timeLeft = LONG_BREAK_SECONDS
                } else {
                    phase = PomodoroPhase.SHORT_BREAK
                    timeLeft = SHORT_BREAK_SECONDS
                }
            }
            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                phase = PomodoroPhase.FOCUS
                timeLeft = FOCUS_SECONDS
            }
        }
    }

    fun reset() {
        isRunning = false
        phase = PomodoroPhase.FOCUS
        timeLeft = FOCUS_SECONDS
        completedFocusSessions = 0
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (!isRunning) continue
            if (timeLeft > 0) {
                timeLeft--
                if (timeLeft == 0) {
                    isRunning = false
                    MetronomePlayer.tick(warning = true)
                    advancePhase()
                }
            }
        }
    }

    val phaseLabel = when (phase) {
        PomodoroPhase.FOCUS -> stringResource(R.string.pomodoro_phase_focus)
        PomodoroPhase.SHORT_BREAK -> stringResource(R.string.pomodoro_phase_short_break)
        PomodoroPhase.LONG_BREAK -> stringResource(R.string.pomodoro_phase_long_break)
    }

    // 원형 배경색 (남은 시간 영역)
    val phaseColor = when (phase) {
        PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primaryContainer
        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.tertiaryContainer
        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.secondaryContainer
    }

    // 경과 슬라이스 및 텍스트 색상
    val onPhaseColor = when (phase) {
        PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.onPrimaryContainer
        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.onTertiaryContainer
        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    // 버튼에 쓸 단계별 강조색
    val accentColor = when (phase) {
        PomodoroPhase.FOCUS -> MaterialTheme.colorScheme.primary
        PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.tertiary
        PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.secondary
    }

    val sweepAngle = ((totalSeconds - timeLeft).toFloat() / totalSeconds * 360f).coerceIn(0f, 360f)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_pomodoro)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 원형 파이 타이머
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // 배경 디스크
                    drawCircle(color = phaseColor)

                    // 바깥쪽 링 — 단계 남은 시간 (밝은 커버가 반시계방향으로 줄어듦)
                    val outerRingStroke = radius * 0.22f
                    val outerRingRadius = radius - outerRingStroke / 2f
                    drawCircle(
                        color = onPhaseColor.copy(alpha = 0.35f),
                        radius = outerRingRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = outerRingStroke)
                    )
                    val remainingOuterSweep = (360f - sweepAngle).coerceIn(0f, 360f)
                    if (remainingOuterSweep > 0f) {
                        drawArc(
                            color = phaseColor,
                            startAngle = -90f,
                            sweepAngle = remainingOuterSweep,
                            useCenter = false,
                            topLeft = Offset(cx - outerRingRadius, cy - outerRingRadius),
                            size = Size(outerRingRadius * 2, outerRingRadius * 2),
                            style = Stroke(width = outerRingStroke, cap = StrokeCap.Round)
                        )
                    }
                    // 바깥쪽·가운데 링 사이 간격을 어두운색으로 채움
                    val ringRadius = radius * 0.635f
                    val ringStroke = radius * 0.19f
                    drawCircle(color = onPhaseColor.copy(alpha = 0.35f), radius = outerRingRadius - outerRingStroke / 2f)
                    // 가운데 링이 그려질 영역을 배경색으로 리셋 (겹침으로 인한 색상 차이 방지)
                    drawCircle(color = phaseColor, radius = ringRadius + ringStroke / 2f)

                    // 가운데 링 — 분 내 초 진행 (밝은 커버가 반시계방향으로 줄어듦)
                    drawCircle(
                        color = onPhaseColor.copy(alpha = 0.35f),
                        radius = ringRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = ringStroke)
                    )
                    val handElapsed = (60 - timeLeft % 60) % 60
                    val remainingArcSweep = (60 - handElapsed) / 60f * 360f
                    if (remainingArcSweep > 0f) {
                        drawArc(
                            color = phaseColor,
                            startAngle = -90f,
                            sweepAngle = remainingArcSweep,
                            useCenter = false,
                            topLeft = Offset(cx - ringRadius, cy - ringRadius),
                            size = Size(ringRadius * 2, ringRadius * 2),
                            style = Stroke(width = ringStroke, cap = StrokeCap.Round)
                        )
                    }
                    // 가운데·안쪽 원 사이 간격을 어두운색으로 채움
                    drawCircle(color = onPhaseColor.copy(alpha = 0.35f), radius = ringRadius - ringStroke / 2f)

                    // 안쪽 채워진 원 — 텍스트 배경
                    drawCircle(color = phaseColor, radius = radius * 0.50f)
                }
                // 원 중앙 텍스트
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = phaseLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = onPhaseColor
                    )
                    val minutes = timeLeft / 60
                    val seconds = timeLeft % 60
                    Text(
                        text = "%02d:%02d".format(minutes, seconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = onPhaseColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SessionDots(
                completedFocusSessions = completedFocusSessions,
                isFocusPhase = phase == PomodoroPhase.FOCUS,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = ::reset) {
                    Text(stringResource(R.string.pomodoro_btn_reset))
                }
                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        text = if (isRunning) stringResource(R.string.btn_pause)
                               else stringResource(R.string.btn_start),
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        MetronomePlayer.tick(warning = false)
                        advancePhase()
                    }
                ) {
                    Text(stringResource(R.string.pomodoro_btn_skip))
                }
            }
        }
    }
}

@Composable
private fun SessionDots(
    completedFocusSessions: Int,
    isFocusPhase: Boolean,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(SESSIONS_BEFORE_LONG_BREAK) { index ->
            val filled = index < completedFocusSessions ||
                (isFocusPhase && index == completedFocusSessions)
            Surface(
                modifier = Modifier.size(14.dp),
                shape = CircleShape,
                color = color.copy(alpha = if (filled) 1f else 0.25f)
            ) {}
        }
    }
}
