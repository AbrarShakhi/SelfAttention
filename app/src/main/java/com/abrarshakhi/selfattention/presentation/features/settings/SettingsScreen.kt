package com.abrarshakhi.selfattention.presentation.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.presentation.components.SelectableChip
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(
        settings = state.settings,
        onThemeModeChange = viewModel::setThemeMode,
        onWeekStartDayChange = viewModel::setWeekStartDay,
        onHolidayToggle = viewModel::toggleHoliday,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingsContent(
    settings: AppSettings,
    onThemeModeChange: (ThemeMode) -> Unit,
    onWeekStartDayChange: (DayOfWeek) -> Unit,
    onHolidayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    var weekStartDialogOpen by remember { mutableStateOf(false) }

    // The app's only Scaffold lives in AppRoot, so this screen is content-only. Surface gives it
    // the themed background and is the semantic container the rest of the screen sits on.
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            // ── Appearance ───────────────────────────────────────────────────
            SettingsHeader("Appearance")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = settings.themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ThemeMode.entries.size,
                            ),
                            label = { Text(mode.label()) },
                        )
                    }
                }
                Text(
                    text = "System follows your device's light or dark setting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            SettingsDivider()

            // ── Week ─────────────────────────────────────────────────────────
            SettingsHeader("Week")
            ListItem(
                headlineContent = { Text("Week starts on") },
                supportingContent = {
                    Text(settings.weekStartDay.getDisplayName(TextStyle.FULL, locale))
                },
                modifier = Modifier.clickable { weekStartDialogOpen = true },
            )

            SettingsDivider()

            // ── Weekly holidays ──────────────────────────────────────────────
            SettingsHeader("Weekly holidays")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DayOfWeek.entries.forEach { day ->
                        SelectableChip(
                            label = day.getDisplayName(TextStyle.SHORT, locale),
                            selected = day in settings.weeklyHolidays,
                            onClick = { onHolidayToggle(day) },
                        )
                    }
                }
                Text(
                    text = holidaySummary(settings.weeklyHolidays, locale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (weekStartDialogOpen) {
        WeekStartDialog(
            selected = settings.weekStartDay,
            onSelect = {
                onWeekStartDayChange(it)
                weekStartDialogOpen = false
            },
            onDismiss = { weekStartDialogOpen = false },
        )
    }
}

@Composable
private fun WeekStartDialog(
    selected: DayOfWeek,
    onSelect: (DayOfWeek) -> Unit,
    onDismiss: () -> Unit,
) {
    val locale = LocalLocale.current.platformLocale
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Week starts on") },
        text = {
            // selectableGroup() makes the rows one radio group for accessibility services.
            Column(modifier = Modifier.selectableGroup()) {
                DayOfWeek.entries.forEach { day ->
                    ListItem(
                        headlineContent = { Text(day.getDisplayName(TextStyle.FULL, locale)) },
                        leadingContent = {
                            // null onClick: the row owns the click, so the button is decorative
                            // and is not announced as a separate target.
                            RadioButton(selected = day == selected, onClick = null)
                        },
                        modifier = Modifier.selectable(
                            selected = day == selected,
                            onClick = { onSelect(day) },
                            role = Role.RadioButton,
                        ),
                        // ListItem defaults to `surface`, but AlertDialog's container is
                        // `surfaceContainerHigh` — an opaque row would sit a tone off the dialog
                        // behind it. Transparent lets the dialog's own container show through.
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun SettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .semantics { heading() },
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System"
}

private fun holidaySummary(holidays: Set<DayOfWeek>, locale: Locale): String =
    if (holidays.isEmpty()) {
        "No weekly holidays selected."
    } else {
        holidays.sorted().joinToString { it.getDisplayName(TextStyle.FULL, locale) }
    }
