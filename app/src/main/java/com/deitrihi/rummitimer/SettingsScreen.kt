package com.deitrihi.rummitimer

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: String,
    onThemeChange: (String) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    fruitIndices: List<Int>,
    onFruitChange: (playerIndex: Int, fruitIndex: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(LocaleHelper.getSelectedLanguage(context)) }

    val languageOptions = listOf(
        LocaleHelper.LANG_SYSTEM to stringResource(R.string.language_system),
        LocaleHelper.LANG_KOREAN to stringResource(R.string.language_korean),
        LocaleHelper.LANG_ENGLISH to stringResource(R.string.language_english),
        LocaleHelper.LANG_JAPANESE to stringResource(R.string.language_japanese),
        LocaleHelper.LANG_GERMAN to stringResource(R.string.language_german),
        LocaleHelper.LANG_SPANISH to stringResource(R.string.language_spanish),
        LocaleHelper.LANG_DUTCH to stringResource(R.string.language_dutch),
    )

    val themeOptions = listOf(
        ThemeHelper.THEME_SYSTEM to stringResource(R.string.theme_system),
        ThemeHelper.THEME_LIGHT to stringResource(R.string.theme_light),
        ThemeHelper.THEME_DARK to stringResource(R.string.theme_dark),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        AnalyticsHelper.log(context, AnalyticsHelper.SETTINGS_BACK)
                        onBack()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back)
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
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 언어 섹션
            Text(
                text = stringResource(R.string.language_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
            languageOptions.forEach { (lang, label) ->
                SettingsRadioRow(
                    label = label,
                    selected = selectedLanguage == lang,
                    onClick = {
                        AnalyticsHelper.log(context, AnalyticsHelper.LANGUAGE_CHANGE, "language" to lang)
                        selectedLanguage = lang
                        LocaleHelper.setLanguage(context, lang)
                        (context as? Activity)?.recreate()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 테마 섹션
            Text(
                text = stringResource(R.string.theme_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
            themeOptions.forEach { (theme, label) ->
                SettingsRadioRow(
                    label = label,
                    selected = currentTheme == theme,
                    onClick = {
                        AnalyticsHelper.log(context, AnalyticsHelper.THEME_CHANGE, "theme" to theme)
                        onThemeChange(theme)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 화면 섹션
            Text(
                text = stringResource(R.string.display_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
            SettingsSwitchRow(
                label = stringResource(R.string.keep_screen_on_label),
                checked = keepScreenOn,
                onCheckedChange = onKeepScreenOnChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 과일 아이콘 섹션
            Text(
                text = stringResource(R.string.fruit_icon_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
            repeat(4) { playerIndex ->
                FruitPickerRow(
                    playerIndex = playerIndex,
                    selectedFruitIndex = fruitIndices[playerIndex],
                    onSelect = { fruitIndex -> onFruitChange(playerIndex, fruitIndex) }
                )
                if (playerIndex < 3) Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FruitPickerRow(
    playerIndex: Int,
    selectedFruitIndex: Int,
    onSelect: (Int) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "P${playerIndex + 1}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(32.dp)
        )
        FRUIT_EMOJIS.forEachIndexed { fruitIndex, emoji ->
            FruitChip(
                emoji = emoji,
                selected = selectedFruitIndex == fruitIndex,
                onClick = {
                    AnalyticsHelper.log(context, AnalyticsHelper.FRUIT_CHANGE, "player" to playerIndex, "fruit" to fruitIndex)
                    onSelect(fruitIndex)
                }
            )
        }
    }
}

@Composable
private fun FruitChip(
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}
