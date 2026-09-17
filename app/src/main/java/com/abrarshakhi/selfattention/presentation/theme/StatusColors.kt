package com.abrarshakhi.selfattention.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus

/** One Material 3 color family: the four roles every accent color needs. */
@Immutable
data class StatusColor(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

/**
 * Attendance status colors, modelled as Material 3 *extended colors*.
 *
 * Present/Absent/Holiday carry meaning no built-in M3 role expresses, so each gets a full family
 * generated from its own HCT hue and chroma — the shape Material Theme Builder emits for custom
 * colors. Reach them through [AppTheme.status], never as top-level constants: that is what makes
 * them follow the active theme.
 *
 * Pair [StatusColor.color] with [StatusColor.onColor], and [StatusColor.colorContainer] with
 * [StatusColor.onColorContainer]. Mixing the two pairs is how contrast bugs get in.
 */
@Immutable
data class StatusColors(
    val present: StatusColor,
    val absent: StatusColor,
    val holiday: StatusColor,
)

// light: color T40 / onColor T100 / container T90 / onContainer T10
internal val LightStatusColors = StatusColors(
    present = StatusColor(                 // source #4A7C3F → hue 142.465, chroma 41.539
        color = Color(0xFF38692E),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFB9F1A8),
        onColorContainer = Color(0xFF002200),
    ),
    absent = StatusColor(                  // source #B34842 → hue 23.379, chroma 54.748
        color = Color(0xFFA33C37),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFFFDAD5),
        onColorContainer = Color(0xFF410003),
    ),
    holiday = StatusColor(                 // source #D49B3A → hue 77.267, chroma 43.790
        color = Color(0xFF815600),
        onColor = Color(0xFFFFFFFF),
        colorContainer = Color(0xFFFFDDAA),
        onColorContainer = Color(0xFF281800),
    ),
)

// dark: color T80 / onColor T20 / container T30 / onContainer T90
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

/** Theme accessor for tokens `MaterialTheme` does not carry. */
object AppTheme {
    val status: StatusColors
        @Composable @ReadOnlyComposable
        get() = LocalStatusColors.current
}

/** The color family for a status, so callers pick container and on-container as a matched pair. */
fun StatusColors.forStatus(status: AttendanceStatus): StatusColor = when (status) {
    AttendanceStatus.PRESENT -> present
    AttendanceStatus.ABSENT -> absent
    AttendanceStatus.HOLIDAY -> holiday
}
