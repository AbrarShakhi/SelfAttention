package com.abrarshakhi.selfattention.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.abrarshakhi.selfattention.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Today,
    onPrimary = Paper,
    primaryContainer = Paper,
    onPrimaryContainer = Ink,
    secondary = Present,
    onSecondary = Paper,
    tertiary = Holiday,
    background = PaperDim,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDim,
    onSurfaceVariant = Ink2,
    outline = Ink3,
    error = Absent,
    onError = Paper,
)

private val DarkColors = darkColorScheme(
    primary = Today,
    onPrimary = Paper,
    secondary = Present,
    tertiary = Holiday,
    background = Color(0xFF1A1612),
    surface = Color(0xFF211E1A),
    onSurface = Paper,
    error = Absent,
)

@Composable
fun SelfAttentionTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

