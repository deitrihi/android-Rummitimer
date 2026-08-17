// 2인 대국 타이머(장기·바둑·체스) 제한 시간 설정 화면
package com.deitrihi.rummitimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var janggiMinutes by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getJanggiMinutes(context)) }
    var badukMainMinutes by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getBadukMainMinutes(context)) }
    var badukByoyomiSeconds by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getBadukByoyomiSeconds(context)) }
    var badukByoyomiPeriods by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getBadukByoyomiPeriods(context)) }
    var chessMainMinutes by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getChessMainMinutes(context)) }
    var chessIncrementSeconds by rememberSaveable { mutableIntStateOf(TwoPlayerSetupHelper.getChessIncrementSeconds(context)) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
        AutoFitContent {
        Column(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (gameType) {
                GameType.JANGGI -> {
                    ValueDropdown(
                        label = stringResource(R.string.turn_duration_label),
                        selectedValue = janggiMinutes,
                        options = JANGGI_TIME_OPTIONS_MINUTES,
                        valueLabel = { stringResource(R.string.two_player_time_min_format, it) },
                        onSelect = { janggiMinutes = it }
                    )
                }
                GameType.BADUK -> {
                    ValueDropdown(
                        label = stringResource(R.string.main_time_label),
                        selectedValue = badukMainMinutes,
                        options = BADUK_MAIN_TIME_OPTIONS_MINUTES,
                        valueLabel = { stringResource(R.string.two_player_time_min_format, it) },
                        onSelect = { badukMainMinutes = it }
                    )
                    ValueDropdown(
                        label = stringResource(R.string.byoyomi_time_label),
                        selectedValue = badukByoyomiSeconds,
                        options = BADUK_BYOYOMI_TIME_OPTIONS_SECONDS,
                        valueLabel = { stringResource(R.string.turn_duration_format, it) },
                        onSelect = { badukByoyomiSeconds = it }
                    )
                    ValueDropdown(
                        label = stringResource(R.string.byoyomi_periods_label),
                        selectedValue = badukByoyomiPeriods,
                        options = BADUK_BYOYOMI_PERIODS_OPTIONS,
                        valueLabel = { stringResource(R.string.byoyomi_periods_format, it) },
                        onSelect = { badukByoyomiPeriods = it }
                    )
                }
                GameType.CHESS -> {
                    ValueDropdown(
                        label = stringResource(R.string.main_time_label),
                        selectedValue = chessMainMinutes,
                        options = CHESS_MAIN_TIME_OPTIONS_MINUTES,
                        valueLabel = { stringResource(R.string.two_player_time_min_format, it) },
                        onSelect = { chessMainMinutes = it }
                    )
                    ValueDropdown(
                        label = stringResource(R.string.increment_time_label),
                        selectedValue = chessIncrementSeconds,
                        options = CHESS_INCREMENT_OPTIONS_SECONDS,
                        valueLabel = { stringResource(R.string.turn_duration_format, it) },
                        onSelect = { chessIncrementSeconds = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    when (gameType) {
                        GameType.JANGGI -> {
                            TwoPlayerSetupHelper.setJanggiMinutes(context, janggiMinutes)
                            onStart(janggiMinutes * 60, 0, 0, 0)
                        }
                        GameType.BADUK -> {
                            TwoPlayerSetupHelper.setBadukMainMinutes(context, badukMainMinutes)
                            TwoPlayerSetupHelper.setBadukByoyomiSeconds(context, badukByoyomiSeconds)
                            TwoPlayerSetupHelper.setBadukByoyomiPeriods(context, badukByoyomiPeriods)
                            onStart(badukMainMinutes * 60, badukByoyomiSeconds, badukByoyomiPeriods, 0)
                        }
                        GameType.CHESS -> {
                            TwoPlayerSetupHelper.setChessMainMinutes(context, chessMainMinutes)
                            TwoPlayerSetupHelper.setChessIncrementSeconds(context, chessIncrementSeconds)
                            onStart(chessMainMinutes * 60, 0, 0, chessIncrementSeconds)
                        }
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ValueDropdown(
    label: String,
    selectedValue: Int,
    options: List<Int>,
    valueLabel: @Composable (Int) -> String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valueLabel(selectedValue),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { value ->
                DropdownMenuItem(
                    text = { Text(valueLabel(value)) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}
