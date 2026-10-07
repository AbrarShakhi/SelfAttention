package com.abrarshakhi.selfattention.core.ui.preference

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.connectedButtonShapes
import com.abrarshakhi.selfattention.core.designsystem.component.toggleColors
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.SeedColors
import com.abrarshakhi.selfattention.core.designsystem.theme.fontFamilyFor
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.ColorStyle
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.abrarshakhi.selfattention.core.ui.calendar.WeekdayToggleRow
import java.time.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        modes.forEachIndexed { index, mode ->
            ToggleButton(
                checked = mode == selected,
                onCheckedChange = { onSelect(mode) },
                modifier = Modifier.weight(1f),
                shapes = connectedButtonShapes(index, modes.size),
                colors = toggleColors(),
            ) {
                Icon(mode.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(mode.label, maxLines = 1)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun ColorStyleSelector(
    selected: ColorStyle,
    onSelect: (ColorStyle) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ColorStyle.entries.forEach { style ->
            ToggleButton(
                checked = style == selected,
                onCheckedChange = { onSelect(style) },
                enabled = enabled,
                colors = toggleColors(),
            ) { Text(style.label) }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SeedColorPicker(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(SeedColors) { color ->
            val argb = color.toArgb()
            SeedSwatch(
                color = color,
                selected = argb == selected,
                enabled = enabled,
                onClick = { onSelect(argb) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SeedSwatch(color: Color, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (selected) 45f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "swatchRotation",
    )
    val shape = if (selected) MaterialShapes.Cookie9Sided.toShape() else MaterialShapes.Circle.toShape()
    Box(
        modifier = Modifier
            .size(52.dp)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = "Colour" },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(if (selected) 52.dp else 44.dp)
                .rotate(rotation)
                .background(color.copy(alpha = if (enabled) 1f else 0.38f), shape),
        )
        if (selected) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun FontSelector(
    selected: AppFont,
    onSelect: (AppFont) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(AppFont.entries) { font ->
            FontSample(font = font, selected = font == selected, onClick = { onSelect(font) })
        }
    }
}

@Composable
private fun FontSample(font: AppFont, selected: Boolean, onClick: () -> Unit) {
    val family = fontFamilyFor(font)
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier
            .width(104.dp)
            .animateContentSize(),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = "Aa", fontFamily = family, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = font.label,
                fontFamily = family,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun WeekStartSelector(
    selected: DayOfWeek,
    onSelect: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WeekdayToggleRow(selected = setOf(selected), onToggle = onSelect)
        Text(
            text = "Weeks start on ${selected.getDisplayName(TextStyle.FULL, locale)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun WeeklyHolidaySelector(
    selected: Set<DayOfWeek>,
    onToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
) {
    val locale = LocalLocale.current.platformLocale
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WeekdayToggleRow(
            selected = selected,
            onToggle = onToggle,
            weekStart = weekStart,
            checkedContainerColor = AppTheme.status.holiday.color,
            checkedContentColor = AppTheme.status.holiday.onColor,
        )
        Text(
            text = if (selected.isEmpty()) {
                "No weekly holidays"
            } else {
                "No classes on " + selected.sorted().joinToString { it.getDisplayName(TextStyle.FULL, locale) }
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val ThemeMode.label: String
    get() = when (this) {
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
        ThemeMode.SYSTEM -> "Auto"
    }

private val ThemeMode.icon: ImageVector
    get() = when (this) {
        ThemeMode.LIGHT -> Icons.Rounded.LightMode
        ThemeMode.DARK -> Icons.Rounded.DarkMode
        ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
    }
