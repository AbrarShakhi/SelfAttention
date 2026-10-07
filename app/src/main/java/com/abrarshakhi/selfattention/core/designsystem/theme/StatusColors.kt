package com.abrarshakhi.selfattention.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.materialkolor.ktx.harmonize

@Immutable
data class StatusColor(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

@Immutable
data class StatusColors(
    val present: StatusColor,
    val absent: StatusColor,
    val holiday: StatusColor,
)

internal fun statusColors(primary: Color, isDark: Boolean): StatusColors = StatusColors(
    present = statusColor(PresentSeed, primary, isDark),
    absent = statusColor(AbsentSeed, primary, isDark),
    holiday = statusColor(HolidaySeed, primary, isDark),
)

private fun statusColor(seed: Color, primary: Color, isDark: Boolean): StatusColor {
    val scheme = dynamicColorScheme(
        seedColor = seed.harmonize(primary),
        isDark = isDark,
        style = PaletteStyle.Fidelity,
    )
    return StatusColor(
        color = scheme.primary,
        onColor = scheme.onPrimary,
        colorContainer = scheme.primaryContainer,
        onColorContainer = scheme.onPrimaryContainer,
    )
}

val LocalStatusColors = staticCompositionLocalOf {
    statusColors(primary = SeedColors.first(), isDark = false)
}

object AppTheme {
    val status: StatusColors
        @Composable @ReadOnlyComposable
        get() = LocalStatusColors.current
}

fun StatusColors.forStatus(status: AttendanceStatus): StatusColor = when (status) {
    AttendanceStatus.PRESENT -> present
    AttendanceStatus.ABSENT -> absent
    AttendanceStatus.HOLIDAY -> holiday
}
