package com.abrarshakhi.selfattention.feature.widget.common

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.luminance
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.color.ColorProvider
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import com.abrarshakhi.selfattention.core.designsystem.theme.StatusColor
import com.abrarshakhi.selfattention.core.designsystem.theme.StatusColors
import com.abrarshakhi.selfattention.core.designsystem.theme.colorSchemeFor
import com.abrarshakhi.selfattention.core.designsystem.theme.statusColors
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.ThemeMode

@Immutable
data class WidgetStatusColor(val color: ColorProvider, val onColor: ColorProvider)

@Immutable
data class WidgetStatusColors(
    val present: WidgetStatusColor,
    val absent: WidgetStatusColor,
    val holiday: WidgetStatusColor,
) {
    fun forStatus(status: AttendanceStatus): WidgetStatusColor = when (status) {
        AttendanceStatus.PRESENT -> present
        AttendanceStatus.ABSENT -> absent
        AttendanceStatus.HOLIDAY -> holiday
    }
}

val LocalWidgetStatusColors = staticCompositionLocalOf<WidgetStatusColors> {
    error("WidgetStatusColors not provided")
}

@Composable
fun SelfAttentionWidgetTheme(settings: AppSettings, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val (light, dark) = remember(settings.themeMode, settings.colorPreferences) { schemes(context, settings) }
    val status = remember(light, dark) { widgetStatusColors(light, dark) }
    CompositionLocalProvider(LocalWidgetStatusColors provides status) {
        GlanceTheme(colors = ColorProviders(light = light, dark = dark), content = content)
    }
}

private fun schemes(context: Context, settings: AppSettings): Pair<ColorScheme, ColorScheme> {
    val light = colorSchemeFor(context, settings.colorPreferences, isDark = false)
    val dark = colorSchemeFor(context, settings.colorPreferences, isDark = true)
    return when (settings.themeMode) {
        ThemeMode.LIGHT -> light to light
        ThemeMode.DARK -> dark to dark
        ThemeMode.SYSTEM -> light to dark
    }
}

private fun widgetStatusColors(light: ColorScheme, dark: ColorScheme): WidgetStatusColors {
    val day = statusColors(light.primary, isDark = light.surface.luminance() < 0.5f)
    val night = statusColors(dark.primary, isDark = dark.surface.luminance() < 0.5f)
    fun pair(pick: (StatusColors) -> StatusColor) = WidgetStatusColor(
        color = ColorProvider(day = pick(day).color, night = pick(night).color),
        onColor = ColorProvider(day = pick(day).onColor, night = pick(night).onColor),
    )
    return WidgetStatusColors(present = pair { it.present }, absent = pair { it.absent }, holiday = pair { it.holiday })
}
