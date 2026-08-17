// 일반 타이머 화면 — 카운트다운, 시간 직접 설정
package com.deitrihi.rummitimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
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

private enum class GeneralTimerState { IDLE, RUNNING, PAUSED, DONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralTimerScreen(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var flashVisible by remember { mutableStateOf(false) }

    fun triggerFlash() = scope.launch {
        repeat(3) {
            flashVisible = true
            kotlinx.coroutines.delay(200)
            flashVisible = false
            kotlinx.coroutines.delay(200)
        }
    }

    var inputHours by rememberSaveable { mutableIntStateOf(0) }
    var inputMinutes by rememberSaveable { mutableIntStateOf(5) }
    var inputSeconds by rememberSaveable { mutableIntStateOf(0) }
    var totalSeconds by rememberSaveable { mutableIntStateOf(0) }
    var timeLeft by rememberSaveable { mutableIntStateOf(0) }
    var timerState by rememberSaveable { mutableStateOf(GeneralTimerState.IDLE) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (timerState == GeneralTimerState.RUNNING && timeLeft > 0) {
                timeLeft--
                if (timeLeft == 0) {
                    timerState = GeneralTimerState.DONE
                    AlertHelper.triggerAlerts(context, warning = true) { triggerFlash() }
                }
            }
        }
    }

    fun start() {
        val total = inputHours * 3600 + inputMinutes * 60 + inputSeconds
        if (total == 0) return
        totalSeconds = total
        timeLeft = total
        timerState = GeneralTimerState.RUNNING
    }

    fun reset() {
        timerState = GeneralTimerState.IDLE
        timeLeft = 0
        totalSeconds = 0
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = timerState != GeneralTimerState.IDLE && !showExitDialog) { showExitDialog = true }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.timer_exit_title)) },
            text = { Text(stringResource(R.string.timer_exit_message)) },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; reset() }) {
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

    // 남은 시간 비율 × 360°의 양수 sweepAngle → 끝점이 반시계 방향으로 후퇴하며 링이 반시계 방향으로 줄어듦
    val sweepAngle = if (totalSeconds > 0) timeLeft.toFloat() / totalSeconds * 360f else 0f

    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
    val accentColor = MaterialTheme.colorScheme.primary
    val flashColor = MaterialTheme.colorScheme.error

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_general_timer)) },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.menu_open)
                        )
                    }
                }
            )
        },
        bottomBar = { BannerAd(modifier = Modifier.fillMaxWidth()) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
        AutoFitContent {
        Column(
            modifier = Modifier.padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (timerState == GeneralTimerState.IDLE) {
                TimeInputRow(
                    hours = inputHours,
                    minutes = inputMinutes,
                    seconds = inputSeconds,
                    onHoursChange = { inputHours = it },
                    onMinutesChange = { inputMinutes = it },
                    onSecondsChange = { inputSeconds = it }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = ::start,
                    enabled = inputHours + inputMinutes + inputSeconds > 0
                ) {
                    Text(
                        text = stringResource(R.string.btn_start),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2f
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val strokeWidth = radius * 0.18f
                        val ringRadius = radius - strokeWidth / 2f

                        drawCircle(color = containerColor)
                        drawCircle(
                            color = onContainerColor.copy(alpha = 0.25f),
                            radius = ringRadius,
                            center = Offset(cx, cy),
                            style = Stroke(width = strokeWidth)
                        )
                        if (sweepAngle != 0f) {
                            drawArc(
                                color = accentColor,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(cx - ringRadius, cy - ringRadius),
                                size = Size(ringRadius * 2, ringRadius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        drawCircle(color = containerColor, radius = radius * 0.62f)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (timerState == GeneralTimerState.DONE) {
                            Text(
                                text = stringResource(R.string.general_timer_done),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = onContainerColor
                            )
                        } else {
                            val h = timeLeft / 3600
                            val m = (timeLeft % 3600) / 60
                            val s = timeLeft % 60
                            Text(
                                text = if (totalSeconds >= 3600) "%02d:%02d:%02d".format(h, m, s)
                                       else "%02d:%02d".format(m, s),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = onContainerColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = ::reset) {
                        Text(stringResource(R.string.pomodoro_btn_reset))
                    }
                    if (timerState != GeneralTimerState.DONE) {
                        Button(
                            onClick = {
                                timerState = if (timerState == GeneralTimerState.RUNNING)
                                    GeneralTimerState.PAUSED else GeneralTimerState.RUNNING
                            }
                        ) {
                            Text(
                                text = if (timerState == GeneralTimerState.RUNNING)
                                    stringResource(R.string.btn_pause)
                                else stringResource(R.string.btn_resume),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        }
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
private fun TimeInputRow(
    hours: Int,
    minutes: Int,
    seconds: Int,
    onHoursChange: (Int) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onSecondsChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeUnitColumn(value = hours, label = "h", max = 23, onValueChange = onHoursChange)
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        TimeUnitColumn(value = minutes, label = "m", max = 59, onValueChange = onMinutesChange)
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        TimeUnitColumn(value = seconds, label = "s", max = 59, onValueChange = onSecondsChange)
    }
}

@Composable
private fun TimeUnitColumn(
    value: Int,
    label: String,
    max: Int,
    onValueChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TextButton(onClick = { onValueChange(if (value >= max) 0 else value + 1) }) {
            Text("▲", style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            text = "%02d".format(value),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { onValueChange(if (value <= 0) max else value - 1) }) {
            Text("▼", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
