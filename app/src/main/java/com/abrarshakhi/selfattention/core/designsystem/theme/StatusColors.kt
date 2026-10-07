package com.abrarshakhi.selfattention.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.abrarshakhi.selfattention.core.model.AttendanceStatus

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

internal val LightStatusColors = StatusColors(
    present = StatusColor(
        color = Color(0xFF38692E),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFB9F1A8),
        onColorContainer = Color(0xFF002200),
    ),
    absent = StatusColor(
        color = Color(0xFFA33C37),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFFFDAD5),
        onColorContainer = Color(0xFF410003),
    ),
    holiday = StatusColor(
        color = Color(0xFF815600),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFFFDDAA),
        onColorContainer = Color(0xFF281800),
    ),
)

internal val DarkStatusColors = StatusColors(
    present = StatusColor(
        color = Color(0xFF9ED58E),
        onColor = Color(0xFF063904),
        colorContainer = Color(0xFF205119),
        onColorContainer = Color(0xFFB9F1A8),
    ),
    absent = StatusColor(
        color = Color(0xFFFFB3AA),
        onColor = Color(0xFF630C0E),
        colorContainer = Color(0xFF832522),
        onColorContainer = Color(0xFFFFDAD5),
    ),
    holiday = StatusColor(
        color = Color(0xFFF9BC58),
        onColor = Color(0xFF442B00),
        colorContainer = Color(0xFF614000),
        onColorContainer = Color(0xFFFFDDAA),
    ),
)

val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }

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
