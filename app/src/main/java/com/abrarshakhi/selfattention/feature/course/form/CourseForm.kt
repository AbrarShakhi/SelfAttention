package com.abrarshakhi.selfattention.feature.course.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.connectedButtonShapes
import com.abrarshakhi.selfattention.core.designsystem.component.toggleColors
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.ui.calendar.WeekdayToggleRow
import com.abrarshakhi.selfattention.core.ui.course.CourseAvatar
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CourseForm(
    state: CourseFormState,
    onEvent: (CourseFormEvent) -> Unit,
    onReminderEnabled: () -> Unit,
    modifier: Modifier = Modifier,
    courseId: Long = 0,
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard(state = state, courseId = courseId)

        FormSection(icon = Icons.Rounded.School, title = "Course") {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(CourseFormEvent.NameChanged(it)) },
                label = { Text("Name") },
                placeholder = { Text("Databases") },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.code,
                onValueChange = { onEvent(CourseFormEvent.CodeChanged(it)) },
                label = { Text("Code (optional)") },
                placeholder = { Text("CS-201") },
                leadingIcon = { Icon(Icons.Rounded.Badge, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        FormSection(icon = Icons.Rounded.Schedule, title = "When") {
            Text("Class days", style = MaterialTheme.typography.labelLarge)
            WeekdayToggleRow(
                selected = state.days,
                onToggle = { onEvent(CourseFormEvent.DayToggled(it)) },
            )
            Text("Starts at", style = MaterialTheme.typography.labelLarge)
            TimeButton(time = state.classTime, onClick = { showTimePicker = true })
            Text("Length", style = MaterialTheme.typography.labelLarge)
            MinuteOptions(
                options = CourseFormState.DurationOptions,
                selected = state.durationMinutes,
                onSelect = { onEvent(CourseFormEvent.DurationChanged(it)) },
            )
        }

        FormSection(
            icon = Icons.Rounded.Notifications,
            title = "Reminder",
            trailing = {
                Switch(
                    checked = state.hasReminder,
                    onCheckedChange = { enabled ->
                        onEvent(CourseFormEvent.ReminderToggled(enabled))
                        if (enabled) onReminderEnabled()
                    },
                )
            },
        ) {
            AnimatedVisibility(
                visible = state.hasReminder,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Minutes before class", style = MaterialTheme.typography.labelLarge)
                    MinuteOptions(
                        options = CourseFormState.ReminderOptions,
                        selected = state.reminderMinutesBefore,
                        onSelect = { onEvent(CourseFormEvent.ReminderLeadChanged(it)) },
                    )
                }
            }
            if (!state.hasReminder) {
                Text(
                    text = "You'll still be asked whether you attended after each class.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showTimePicker) {
        ClassTimePickerDialog(
            initial = state.classTime,
            onConfirm = {
                onEvent(CourseFormEvent.TimeChanged(it))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun SummaryCard(state: CourseFormState, courseId: Long) {
    val locale = LocalLocale.current.platformLocale
    val preview = remember(state.name, courseId) {
        Course(id = courseId, name = state.name.ifBlank { "?" }, code = "", scheduleDays = emptyList(), classHour = 0, classMinute = 0)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CourseAvatar(
                course = preview,
                size = 64.dp,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
            Text(text = state.sentence(locale), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun FormSection(
    icon: ImageVector,
    title: String,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.weight(1f),
                )
                trailing?.invoke()
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimeButton(time: LocalTime, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        shapes = ButtonDefaults.shapes(),
        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(
            text = time.clockLabel(),
            style = MaterialTheme.typography.headlineSmallEmphasized,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MinuteOptions(options: List<Int>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        options.forEachIndexed { index, minutes ->
            ToggleButton(
                checked = minutes == selected,
                onCheckedChange = { onSelect(minutes) },
                modifier = Modifier.weight(1f),
                shapes = connectedButtonShapes(index, options.size),
                colors = toggleColors(),
            ) { Text("${minutes}m", maxLines = 1) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassTimePickerDialog(
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timeState = rememberTimePickerState(initial.hour, initial.minute, is24Hour = true)
    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
    TimePickerDialog(
        onDismissRequest = onDismiss,
        title = { TimePickerDialogDefaults.Title(displayMode = displayMode) },
        modeToggleButton = {
            TimePickerDialogDefaults.DisplayModeToggle(
                displayMode = displayMode,
                onDisplayModeChange = {
                    displayMode = if (displayMode == TimePickerDisplayMode.Picker) {
                        TimePickerDisplayMode.Input
                    } else {
                        TimePickerDisplayMode.Picker
                    }
                },
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(LocalTime.of(timeState.hour, timeState.minute)) }) { Text("Set") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) {
        if (displayMode == TimePickerDisplayMode.Picker) TimePicker(state = timeState) else TimeInput(state = timeState)
    }
}

private fun CourseFormState.sentence(locale: Locale): String {
    val title = name.trim().ifBlank { "Your course" }
    val withCode = if (code.isBlank()) title else "$title · ${code.trim()}"
    val schedule = if (days.isEmpty()) {
        "Pick the days it meets"
    } else {
        "Every ${days.sorted().joinToString(", ") { it.getDisplayName(TextStyle.SHORT, locale) }} at ${classTime.clockLabel()}"
    }
    return "$withCode\n$schedule"
}
