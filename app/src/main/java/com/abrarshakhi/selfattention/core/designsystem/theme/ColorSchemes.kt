package com.abrarshakhi.selfattention.core.designsystem.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import com.abrarshakhi.selfattention.core.model.ColorPreferences
import com.abrarshakhi.selfattention.core.model.ColorStyle
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec

val supportsWallpaperColors: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun colorSchemeFor(context: Context, colors: ColorPreferences, isDark: Boolean): ColorScheme {
    if (colors.useWallpaperColors && supportsWallpaperColors) {
        val wallpaper = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        return if (isDark && colors.pureBlack) wallpaper.withPureBlack() else wallpaper
    }
    return dynamicColorScheme(
        seedColor = Color(colors.seedColor),
        isDark = isDark,
        isAmoled = colors.pureBlack,
        style = colors.style.toPaletteStyle(),
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
    )
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
