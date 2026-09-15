package com.abrarshakhi.selfattention.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.presentation.components.SectionLabel
import com.abrarshakhi.selfattention.presentation.theme.CaveatFamily
import com.abrarshakhi.selfattention.presentation.theme.Ink
import com.abrarshakhi.selfattention.presentation.theme.Ink2
import com.abrarshakhi.selfattention.presentation.theme.Ink3
import com.abrarshakhi.selfattention.presentation.theme.Today
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Settings",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.headlineLarge,
            )
        }

        HorizontalDivider(color = Ink3)

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Week starts on
            SettingsSection(title = "WEEK STARTS ON") {
                var expanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .border(1.5.dp, Ink3, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        settings.weekStartDay.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text("▼", style = MaterialTheme.typography.bodySmall, color = Ink2)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DayOfWeek.values().forEach { day ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    day.getDisplayName(
                                        TextStyle.FULL,
                                        Locale.getDefault()
                                    )
                                )
                            },
                            onClick = {
                                viewModel.setWeekStartDay(day)
                                expanded = false
                            },
                        )
                    }
                }
            }

            // Weekly holidays
            SettingsSection(title = "WEEKLY HOLIDAYS") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DayOfWeek.values().forEach { day ->
                        val selected = settings.weeklyHolidays.contains(day)
                        DayToggleChip(
                            label = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            selected = selected,
                            onClick = { viewModel.toggleHoliday(day) },
                        )
                    }
                }
            }

            // Theme
            SettingsSection(title = "THEME") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.values().forEach { mode ->
                        val selected = settings.themeMode == mode
                        DayToggleChip(
                            label = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            selected = selected,
                            onClick = { viewModel.setThemeMode(mode) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel(title)
        content()
    }
}

@Composable
private fun DayToggleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Today else Color.Transparent
    val textColor = if (selected) Color.White else Ink
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        modifier = Modifier
            .border(1.5.dp, if (selected) Today else Ink3, RoundedCornerShape(20.dp))
            .background(bg, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}
