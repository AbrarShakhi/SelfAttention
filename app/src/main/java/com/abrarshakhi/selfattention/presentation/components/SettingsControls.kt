package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.presentation.theme.fontFamilyFor
import java.time.DayOfWeek
import java.time.format.TextStyle

/**
 * The individual preference controls, shared by the settings screen and onboarding so the two
 * cannot drift apart.
 */

@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        ThemeMode.entries.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                label = { Text(mode.label()) },
            )
        }
    }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System"
}

@Composable
fun FontSelector(
    selected: AppFont,
    onSelect: (AppFont) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text("Font") },
        supportingContent = { Text(selected.label) },
        modifier = modifier.clickable { open = true },
    )
    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text("Font") },
            text = {
                // Each row previews its own typeface, so the choice can be judged by eye. The
                // downloadable families resolve independently, so rows may fill in a beat apart.
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .verticalScroll(rememberScrollState()),
                ) {
                    AppFont.entries.forEach { font ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = font.label,
                                    fontFamily = fontFamilyFor(font),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            },
                            supportingContent = if (font == AppFont.SYSTEM) {
                                { Text("Always available, no download") }
                            } else {
                                null
                            },
                            leadingContent = {
                                RadioButton(selected = font == selected, onClick = null)
                            },
                            modifier = Modifier.selectable(
                                selected = font == selected,
                                onClick = { onSelect(font); open = false },
                                role = Role.RadioButton,
                            ),
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        )
    }
}

@Composable
fun WeekStartSelector(
    selected: DayOfWeek,
    onSelect: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    var open by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("Week starts on") },
        supportingContent = { Text(selected.getDisplayName(TextStyle.FULL, locale)) },
        modifier = modifier.clickable { open = true },
    )
    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text("Week starts on") },
            text = {
                // selectableGroup() makes the rows one radio group for accessibility services.
                Column(modifier = Modifier.selectableGroup()) {
                    DayOfWeek.entries.forEach { day ->
                        ListItem(
                            headlineContent = { Text(day.getDisplayName(TextStyle.FULL, locale)) },
                            leadingContent = {
                                // null onClick: the row owns the click, so the button is
                                // decorative and is not announced as a separate target.
                                RadioButton(selected = day == selected, onClick = null)
                            },
                            modifier = Modifier.selectable(
                                selected = day == selected,
                                onClick = { onSelect(day); open = false },
                                role = Role.RadioButton,
                            ),
                            // ListItem defaults to `surface`, a tone off the dialog's
                            // `surfaceContainerHigh`; transparent lets the dialog show through.
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeeklyHolidaySelector(
    selected: Set<DayOfWeek>,
    onToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DayOfWeek.entries.forEach { day ->
                SelectableChip(
                    label = day.getDisplayName(TextStyle.SHORT, locale),
                    selected = day in selected,
                    onClick = { onToggle(day) },
                )
            }
        }
        Text(
            text = if (selected.isEmpty()) {
                "No weekly holidays selected."
            } else {
                selected.sorted().joinToString { it.getDisplayName(TextStyle.FULL, locale) }
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
