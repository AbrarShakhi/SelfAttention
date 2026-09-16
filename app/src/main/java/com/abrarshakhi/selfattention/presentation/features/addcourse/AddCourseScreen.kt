package com.abrarshakhi.selfattention.presentation.features.addcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.abrarshakhi.selfattention.presentation.components.SectionLabel
import com.abrarshakhi.selfattention.presentation.theme.CaveatFamily
import com.abrarshakhi.selfattention.presentation.theme.Ink
import com.abrarshakhi.selfattention.presentation.theme.Ink2
import com.abrarshakhi.selfattention.presentation.theme.Ink3
import com.abrarshakhi.selfattention.presentation.theme.Today
import java.time.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddCourseScreen(
    onDone: () -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val timePickerState = rememberTimePickerState(
        initialHour = state.classHour,
        initialMinute = state.classMinute,
        is24Hour = true,
    )

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Sentence form
        SentenceRow {
            InlineLabel("I have")
            InlineTextField(
                value = state.name,
                placeholder = "Subject name",
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.weight(1f),
            )
        }

        SentenceRow {
            InlineLabel("code")
            InlineTextField(
                value = state.code,
                placeholder = "e.g. CS-201",
                onValueChange = viewModel::onCodeChange,
                modifier = Modifier.width(120.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InlineLabel("every")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayOfWeek.entries.forEach { day ->
                    val selected = state.selectedDays.contains(day)
                    DayChip(
                        label = day.getDisplayName(
                            TextStyle.SHORT,
                            LocalLocale.current.platformLocale
                        ),
                        selected = selected,
                        onClick = { viewModel.toggleDay(day) },
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InlineLabel("at")
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectorColor = Today,
                ),
            )
            LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                viewModel.onTimeChange(timePickerState.hour, timePickerState.minute)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel("REMIND ME")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = state.hasReminder,
                    onCheckedChange = viewModel::onReminderToggle,
                )
                Spacer(Modifier.width(12.dp))
                if (state.hasReminder) {
                    Text(
                        text = "${state.reminderMinutesBefore} min before",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = "No reminder",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink2,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = viewModel::save,
            enabled = state.canSave && !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Today),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = if (state.isSaving) "Saving…" else "Add Subject",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        state.error?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SentenceRow(content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun InlineLabel(text: String) {
    Text(
        text = text,
        fontFamily = CaveatFamily,
        style = MaterialTheme.typography.titleLarge,
        color = Ink2,
    )
}

@Composable
private fun InlineTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Ink3) },
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        modifier = modifier,
    )
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit) {
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
