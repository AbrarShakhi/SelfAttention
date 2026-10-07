package com.abrarshakhi.selfattention.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.ColorPreferences
import com.abrarshakhi.selfattention.core.model.ColorStyle
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.animateColorScheme
import com.materialkolor.rememberDynamicColorScheme

val supportsWallpaperColors: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

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
    val colorScheme = animateColorScheme(appColorScheme(colors, isDark))
    val statusColors = remember(colorScheme.primary, isDark) {
        statusColors(colorScheme.primary, isDark)
    }
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

@Composable
private fun appColorScheme(colors: ColorPreferences, isDark: Boolean): ColorScheme {
    val seeded = rememberDynamicColorScheme(
        seedColor = Color(colors.seedColor),
        isDark = isDark,
        isAmoled = colors.pureBlack,
        style = colors.style.toPaletteStyle(),
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
    )
    if (!colors.useWallpaperColors || !supportsWallpaperColors) return seeded

    val context = LocalContext.current
    return remember(context, isDark, colors.pureBlack) {
        val wallpaper = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        if (isDark && colors.pureBlack) wallpaper.withPureBlack() else wallpaper
    }
}

private fun ColorScheme.withPureBlack(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceDim = Color.Black,
)

fun ColorStyle.toPaletteStyle(): PaletteStyle = when (this) {
    ColorStyle.TONAL_SPOT -> PaletteStyle.TonalSpot
    ColorStyle.EXPRESSIVE -> PaletteStyle.Expressive
    ColorStyle.VIBRANT -> PaletteStyle.Vibrant
    ColorStyle.FIDELITY -> PaletteStyle.Fidelity
    ColorStyle.RAINBOW -> PaletteStyle.Rainbow
    ColorStyle.MONOCHROME -> PaletteStyle.Monochrome
}
