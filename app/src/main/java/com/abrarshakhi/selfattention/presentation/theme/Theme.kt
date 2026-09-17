package com.abrarshakhi.selfattention.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceTint = primaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
    primaryFixed = primaryFixed,
    primaryFixedDim = primaryFixedDim,
    onPrimaryFixed = onPrimaryFixed,
    onPrimaryFixedVariant = onPrimaryFixedVariant,
    secondaryFixed = secondaryFixed,
    secondaryFixedDim = secondaryFixedDim,
    onSecondaryFixed = onSecondaryFixed,
    onSecondaryFixedVariant = onSecondaryFixedVariant,
    tertiaryFixed = tertiaryFixed,
    tertiaryFixedDim = tertiaryFixedDim,
    onTertiaryFixed = onTertiaryFixed,
    onTertiaryFixedVariant = onTertiaryFixedVariant,
)

private val DarkColors = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceTint = primaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
    primaryFixed = primaryFixed,
    primaryFixedDim = primaryFixedDim,
    onPrimaryFixed = onPrimaryFixed,
    onPrimaryFixedVariant = onPrimaryFixedVariant,
    secondaryFixed = secondaryFixed,
    secondaryFixedDim = secondaryFixedDim,
    onSecondaryFixed = onSecondaryFixed,
    onSecondaryFixedVariant = onSecondaryFixedVariant,
    tertiaryFixed = tertiaryFixed,
    tertiaryFixedDim = tertiaryFixedDim,
    onTertiaryFixed = onTertiaryFixed,
    onTertiaryFixedVariant = onTertiaryFixedVariant,
)

/**
 * Resolves a [ThemeMode] to a light/dark boolean.
 *
 * Hoisted out of [SelfAttentionTheme] so `MainActivity` can drive `enableEdgeToEdge` with the same
 * answer the theme uses — otherwise the system bars never update when the user changes the mode.
 */
@Composable
@ReadOnlyComposable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

@Composable
fun SelfAttentionTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    appFont: AppFont = AppFont.Default,
    // Off by default: Material You would replace the brand palette. Wired up so it can be
    // exposed as a user setting later without touching this file again.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = themeMode.isDark()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    // Rebuilding the whole type scale allocates 15 TextStyles; only do it when the font changes.
    val typography = remember(appFont) { appTypography(appFont) }

    CompositionLocalProvider(
        LocalStatusColors provides if (darkTheme) DarkStatusColors else LightStatusColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = AppShapes,
            content = content,
        )
    }
}
