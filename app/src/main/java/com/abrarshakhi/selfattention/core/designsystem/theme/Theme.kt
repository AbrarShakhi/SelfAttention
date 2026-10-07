package com.abrarshakhi.selfattention.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.ColorPreferences
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.materialkolor.ktx.animateColorScheme

@Composable
@ReadOnlyComposable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelfAttentionTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    appFont: AppFont = AppFont.Default,
    colors: ColorPreferences = ColorPreferences(),
    content: @Composable () -> Unit,
) {
    val isDark = themeMode.isDark()
    val context = LocalContext.current
    val target = remember(context, colors, isDark) { colorSchemeFor(context, colors, isDark) }
    val colorScheme = animateColorScheme(target)
    val statusColors = remember(colorScheme.primary, isDark) { statusColors(colorScheme.primary, isDark) }
    val typography = remember(appFont) { appTypography(appFont) }

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            typography = typography,
            content = content,
        )
    }
}
