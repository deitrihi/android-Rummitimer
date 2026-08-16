// 2인 대국 타이머(장기·바둑·체스) 제한 시간 설정 화면
package com.deitrihi.rummitimer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val JANGGI_TIME_OPTIONS_MINUTES = listOf(1, 3, 5, 10, 30)
private val BADUK_MAIN_TIME_OPTIONS_MINUTES = listOf(0, 10, 20, 30, 60)
private val BADUK_BYOYOMI_TIME_OPTIONS_SECONDS = listOf(30, 40, 60)
private val BADUK_BYOYOMI_PERIODS_OPTIONS = listOf(1, 2, 3, 4, 5)
private val CHESS_MAIN_TIME_OPTIONS_MINUTES = listOf(1, 3, 5, 10, 15, 30)
private val CHESS_INCREMENT_OPTIONS_SECONDS = listOf(0, 1, 2, 3, 5, 10, 30)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoPlayerSetupScreen(
    gameType: GameType,
    onStart: (mainTimeSeconds: Int, byoyomiSeconds: Int, byoyomiPeriods: Int, incrementSeconds: Int) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var janggiMinutes by rememberSaveable { mutableIntStateOf(10) }
    var badukMainMinutes by rememberSaveable { mutableIntStateOf(30) }
    var badukByoyomiSeconds by rememberSaveable { mutableIntStateOf(30) }
    var badukByoyomiPeriods by rememberSaveable { mutableIntStateOf(3) }
    var chessMainMinutes by rememberSaveable { mutableIntStateOf(5) }
    var chessIncrementSeconds by rememberSaveable { mutableIntStateOf(3) }

    val titleRes = when (gameType) {
        GameType.JANGGI -> R.string.janggi_setup_title
        GameType.BADUK -> R.string.baduk_setup_title
        GameType.CHESS -> R.string.chess_setup_title
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titleRes)) },
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
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (gameType) {
                GameType.JANGGI -> {
                    Text(
                        text = stringResource(R.string.turn_duration_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        JANGGI_TIME_OPTIONS_MINUTES.forEach { minutes ->
                            FilterChip(
                                selected = janggiMinutes == minutes,
                                onClick = { janggiMinutes = minutes },
                                label = {
                                    Text(stringResource(R.string.two_player_time_min_format, minutes))
                                }
                            )
                        }
                    }
                }
                GameType.BADUK -> {
                    Text(
                        text = stringResource(R.string.main_time_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BADUK_MAIN_TIME_OPTIONS_MINUTES.forEach { minutes ->
                            FilterChip(
                                selected = badukMainMinutes == minutes,
                                onClick = { badukMainMinutes = minutes },
                                label = {
                                    Text(stringResource(R.string.two_player_time_min_format, minutes))
                                }
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.byoyomi_time_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BADUK_BYOYOMI_TIME_OPTIONS_SECONDS.forEach { seconds ->
                            FilterChip(
                                selected = badukByoyomiSeconds == seconds,
                                onClick = { badukByoyomiSeconds = seconds },
                                label = {
                                    Text(stringResource(R.string.turn_duration_format, seconds))
                                }
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.byoyomi_periods_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BADUK_BYOYOMI_PERIODS_OPTIONS.forEach { periods ->
                            FilterChip(
                                selected = badukByoyomiPeriods == periods,
                                onClick = { badukByoyomiPeriods = periods },
                                label = {
                                    Text(stringResource(R.string.byoyomi_periods_format, periods))
                                }
                            )
                        }
                    }
                }
                GameType.CHESS -> {
                    Text(
                        text = stringResource(R.string.main_time_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CHESS_MAIN_TIME_OPTIONS_MINUTES.forEach { minutes ->
                            FilterChip(
                                selected = chessMainMinutes == minutes,
                                onClick = { chessMainMinutes = minutes },
                                label = {
                                    Text(stringResource(R.string.two_player_time_min_format, minutes))
                                }
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.increment_time_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CHESS_INCREMENT_OPTIONS_SECONDS.forEach { seconds ->
                            FilterChip(
                                selected = chessIncrementSeconds == seconds,
                                onClick = { chessIncrementSeconds = seconds },
                                label = {
                                    Text(stringResource(R.string.turn_duration_format, seconds))
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    when (gameType) {
                        GameType.JANGGI -> onStart(janggiMinutes * 60, 0, 0, 0)
                        GameType.BADUK -> onStart(
                            badukMainMinutes * 60,
                            badukByoyomiSeconds,
                            badukByoyomiPeriods,
                            0
                        )
                        GameType.CHESS -> onStart(chessMainMinutes * 60, 0, 0, chessIncrementSeconds)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.btn_start),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
