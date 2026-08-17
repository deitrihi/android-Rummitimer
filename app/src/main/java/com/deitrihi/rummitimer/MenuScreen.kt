// 게임·타이머 종류를 선택하는 메뉴 화면
package com.deitrihi.rummitimer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onSelectRummicube: () -> Unit,
    onSelectJanggi: () -> Unit,
    onSelectBaduk: () -> Unit,
    onSelectChess: () -> Unit,
    onSelectPomodoro: () -> Unit,
    onSelectStopwatch: () -> Unit,
    onSelectGeneralTimer: () -> Unit,
    onSelectSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                MenuSectionHeader(stringResource(R.string.menu_section_board_game))
            }
            item {
                MenuItemRow(
                    icon = "🎲",
                    label = stringResource(R.string.menu_rummicube),
                    comingSoon = false,
                    onClick = onSelectRummicube
                )
            }
            item {
                MenuItemRow(
                    icon = "♟",
                    label = stringResource(R.string.menu_janggi),
                    comingSoon = false,
                    onClick = onSelectJanggi
                )
            }
            item {
                MenuItemRow(
                    icon = "⚫",
                    label = stringResource(R.string.menu_baduk),
                    comingSoon = false,
                    onClick = onSelectBaduk
                )
            }
            item {
                MenuItemRow(
                    icon = "♜",
                    label = stringResource(R.string.menu_chess),
                    comingSoon = false,
                    onClick = onSelectChess
                )
            }

            item {
                MenuSectionHeader(stringResource(R.string.menu_section_general))
            }
            item {
                MenuItemRow(
                    icon = "⏱",
                    label = stringResource(R.string.menu_stopwatch),
                    comingSoon = false,
                    onClick = onSelectStopwatch
                )
            }
            item {
                MenuItemRow(
                    icon = "🍅",
                    label = stringResource(R.string.menu_pomodoro),
                    comingSoon = false,
                    onClick = onSelectPomodoro
                )
            }
            item {
                MenuItemRow(
                    icon = "⏰",
                    label = stringResource(R.string.menu_general_timer),
                    comingSoon = false,
                    onClick = onSelectGeneralTimer
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                MenuItemRow(
                    icon = "⚙️",
                    label = stringResource(R.string.settings_title),
                    comingSoon = false,
                    onClick = onSelectSettings
                )
            }
        }
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).padding(top = 12.dp)
    )
}

@Composable
private fun MenuItemRow(
    icon: String,
    label: String,
    comingSoon: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                fontWeight = if (comingSoon) FontWeight.Normal else FontWeight.SemiBold
            )
        },
        leadingContent = {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)
        },
        trailingContent = if (comingSoon) {
            {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = stringResource(R.string.menu_coming_soon),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        } else null,
        modifier = Modifier.clickable(enabled = !comingSoon, onClick = onClick)
    )
}
