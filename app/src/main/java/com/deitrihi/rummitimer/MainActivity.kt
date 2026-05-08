package com.deitrihi.rummitimer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.deitrihi.rummitimer.ui.theme.RummitimerTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeHelper.getSelectedTheme(this)) }
            val darkTheme = when (themeMode) {
                ThemeHelper.THEME_DARK -> true
                ThemeHelper.THEME_LIGHT -> false
                else -> isSystemInDarkTheme()
            }
            RummitimerTheme(darkTheme = darkTheme) {
                RummitimerApp(
                    currentTheme = themeMode,
                    onThemeChange = { mode ->
                        ThemeHelper.setTheme(this, mode)
                        themeMode = mode
                    }
                )
            }
        }
    }
}

@Composable
fun RummitimerApp(
    currentTheme: String = ThemeHelper.THEME_SYSTEM,
    onThemeChange: (String) -> Unit = {},
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            onBack = { showSettings = false }
        )
    } else {
        HomeScreen(
            onMenuClick = { showSettings = true }
        )
    }
}
