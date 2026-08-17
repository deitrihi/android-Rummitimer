// 스톱워치 화면 — 카운트업, 랩 기록 지원
package com.deitrihi.rummitimer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var elapsedMillis by rememberSaveable { mutableLongStateOf(0L) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    // 구성 변경 시 랩 초기화 허용
    var laps by remember { mutableStateOf(emptyList<Long>()) }
    var showExitDialog by remember { mutableStateOf(false) }
    val isNonInitial = isRunning || elapsedMillis > 0L

    BackHandler(enabled = isNonInitial && !showExitDialog) { showExitDialog = true }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.timer_exit_title)) },
            text = { Text(stringResource(R.string.timer_exit_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    isRunning = false
                    elapsedMillis = 0L
                    laps = emptyList()
                }) {
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

    LaunchedEffect(Unit) {
        var lastTick = System.currentTimeMillis()
        while (true) {
            delay(16L)
            val now = System.currentTimeMillis()
            if (isRunning) elapsedMillis += now - lastTick
            lastTick = now
        }
    }

    val minutes = (elapsedMillis / 60000L).toInt()
    val seconds = ((elapsedMillis % 60000L) / 1000L).toInt()
    val centis = ((elapsedMillis % 1000L) / 10L).toInt()

    val lastLapMs = laps.lastOrNull() ?: 0L
    val currentLapMs = elapsedMillis - lastLapMs
    val lapSec = (currentLapMs / 1000L).toInt()
    val lapCentis = ((currentLapMs % 1000L) / 10L).toInt()

    // 카운트업 → 시계 방향 (sweepAngle 양수): 현재 분 내 경과 초 진행
    val secondFraction = (elapsedMillis % 60000L).toFloat() / 60000f
    val sweepAngle = secondFraction * 360f

    val containerColor = MaterialTheme.colorScheme.tertiaryContainer
    val onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
    val accentColor = MaterialTheme.colorScheme.tertiary

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_stopwatch)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        AutoFitContent(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    if (sweepAngle > 0f) {
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
                    Text(
                        text = "%02d:%02d.%02d".format(minutes, seconds, centis),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = onContainerColor
                    )
                    if (laps.isNotEmpty()) {
                        Text(
                            text = "%02d:%02d.%02d".format(lapSec / 60, lapSec % 60, lapCentis),
                            style = MaterialTheme.typography.bodySmall,
                            color = onContainerColor.copy(alpha = 0.65f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        elapsedMillis = 0L
                        laps = emptyList()
                    },
                    enabled = elapsedMillis > 0L
                ) {
                    Text(stringResource(R.string.pomodoro_btn_reset))
                }
                Button(onClick = { isRunning = !isRunning }) {
                    Text(
                        text = if (isRunning) stringResource(R.string.btn_pause)
                               else stringResource(R.string.btn_start),
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = { if (isRunning) laps = laps + elapsedMillis },
                    enabled = isRunning
                ) {
                    Text(stringResource(R.string.btn_lap))
                }
            }
        }
        }

            if (laps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(laps.asReversed()) { revIdx, lapTotal ->
                        val lapIndex = laps.size - 1 - revIdx
                        val lapDuration = lapTotal - (if (lapIndex > 0) laps[lapIndex - 1] else 0L)
                        val dSec = (lapDuration / 1000L).toInt()
                        val dCentis = ((lapDuration % 1000L) / 10L).toInt()
                        val tSec = (lapTotal / 1000L).toInt()
                        val tCentis = ((lapTotal % 1000L) / 10L).toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.stopwatch_lap_format, lapIndex + 1),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "%02d:%02d.%02d".format(dSec / 60, dSec % 60, dCentis),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "%02d:%02d.%02d".format(tSec / 60, tSec % 60, tCentis),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
