package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/** Every slot in the sentence is this tall, so the line reads evenly however it wraps. */
private val SlotHeight = 44.dp
private val ReminderOptions = listOf(5, 10, 15, 30, 60)

/**
 * The sentence-style course form, shared by Add Course and the course editor.
 *
 * Stateless: callers own the values and decide what Confirm/Delete do.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CourseForm(
    name: String,
    code: String,
    selectedDays: Set<DayOfWeek>,
    classHour: Int,
    classMinute: Int,
    hasReminder: Boolean,
    reminderMinutesBefore: Int,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onToggleDay: (DayOfWeek) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onReminderToggle: (Boolean) -> Unit,
    onReminderMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    var showTimePicker by remember { mutableStateOf(false) }
    var showDayPicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SentenceText("I have")
            SentenceField(name, "course name", onNameChange, 168.dp)
            SentenceText("class with code")
            SentenceField(code, "code", onCodeChange, 104.dp)
            SentenceText("on every")
            SentenceToken(
                text = selectedDays.summary(locale),
                isPlaceholder = selectedDays.isEmpty(),
                onClick = { showDayPicker = true },
            )
            SentenceText("at")
            SentenceToken(
                text = LocalTime.of(classHour, classMinute)
                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                isPlaceholder = false,
                onClick = { showTimePicker = true },
            )
            SentenceText(".")
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = hasReminder, onCheckedChange = onReminderToggle)
                Spacer(Modifier.width(12.dp))
                SentenceText("I want to get reminded")
            }
            if (hasReminder) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    SentenceToken(
                        text = "$reminderMinutesBefore min",
                        isPlaceholder = false,
                        onClick = { showReminderPicker = true },
                    )
                    SentenceText("before class.")
                }
            }
        }
    }

    if (showTimePicker) {
        ClassTimePickerDialog(
            initialHour = classHour,
            initialMinute = classMinute,
            onConfirm = { h, m -> onTimeChange(h, m); showTimePicker = false },
            onDismiss = { showTimePicker = false },
        )
    }
    if (showDayPicker) {
        DayPickerDialog(selectedDays, onToggleDay) { showDayPicker = false }
    }
    if (showReminderPicker) {
        ReminderPickerDialog(
            selected = reminderMinutesBefore,
            onSelect = { onReminderMinutesChange(it); showReminderPicker = false },
            onDismiss = { showReminderPicker = false },
        )
    }
}

// ── sentence pieces ──────────────────────────────────────────────────────────

@Composable
private fun SentenceText(text: String) {
    Box(modifier = Modifier.height(SlotHeight), contentAlignment = Alignment.Center) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

/** An inline, underlined field so the sentence keeps flowing instead of breaking into a form. */
@Composable
private fun SentenceField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    width: Dp,
) {
    val textStyle: TextStyle = MaterialTheme.typography.titleMedium
        .copy(color = MaterialTheme.colorScheme.onSurface)

    Column(
        modifier = Modifier
            .width(width)
            .height(SlotHeight),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
            CompositionLocalProvider(LocalTextStyle provides textStyle) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

/** A tappable value in the sentence that opens a picker. */
@Composable
private fun SentenceToken(text: String, isPlaceholder: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.height(SlotHeight), contentAlignment = Alignment.Center) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (isPlaceholder) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            },
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

// ── pickers ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    // 24-hour to match how times are rendered everywhere else in the app.
    val timeState = rememberTimePickerState(initialHour, initialMinute, is24Hour = true)
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
            TextButton(onClick = { onConfirm(timeState.hour, timeState.minute) }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) {
        if (displayMode == TimePickerDisplayMode.Picker) {
            TimePicker(state = timeState)
        } else {
            TimeInput(state = timeState)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayPickerDialog(
    selected: Set<DayOfWeek>,
    onToggle: (DayOfWeek) -> Unit,
    onDismiss: () -> Unit,
) {
    val locale = LocalLocale.current.platformLocale
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Class days") },
        text = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DayOfWeek.entries.forEach { day ->
                    SelectableChip(
                        label = day.getDisplayName(JavaTextStyle.SHORT, locale),
                        selected = day in selected,
                        onClick = { onToggle(day) },
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } },
    )
}

@Composable
private fun ReminderPickerDialog(
    selected: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remind me before class") },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                ReminderOptions.forEach { minutes ->
                    ListItem(
                        headlineContent = { Text("$minutes minutes") },
                        leadingContent = {
                            RadioButton(selected = minutes == selected, onClick = null)
                        },
                        modifier = Modifier.selectable(
                            selected = minutes == selected,
                            onClick = { onSelect(minutes) },
                            role = Role.RadioButton,
                        ),
                        // Let the dialog's own container show through; ListItem defaults to
                        // `surface`, which sits a tone off `surfaceContainerHigh`.
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun Set<DayOfWeek>.summary(locale: Locale): String =
    if (isEmpty()) {
        "pick days"
    } else {
        sorted().joinToString(", ") { it.getDisplayName(JavaTextStyle.SHORT, locale) }
    }
