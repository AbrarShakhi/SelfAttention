package com.abrarshakhi.selfattention.presentation.features.addcourse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.presentation.components.SelectableChip
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.format.TextStyle as JavaTextStyle

private val SlotHeight = 44.dp
private val ReminderOptions = listOf(5, 10, 15, 30, 60)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCourseScreen(
    onDone: () -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalLocale.current.platformLocale

    var showTimePicker by remember { mutableStateOf(false) }
    var showDayPicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SentenceText("I have")
                SentenceField(
                    value = state.name,
                    placeholder = "course name",
                    onValueChange = viewModel::onNameChange,
                    width = 168.dp,
                )
                SentenceText("class with code")
                SentenceField(
                    value = state.code,
                    placeholder = "code",
                    onValueChange = viewModel::onCodeChange,
                    width = 104.dp,
                )
                SentenceText("on every")
                SentenceToken(
                    text = state.selectedDays.summary(locale),
                    isPlaceholder = state.selectedDays.isEmpty(),
                    onClick = { showDayPicker = true },
                )
                SentenceText("at")
                SentenceToken(
                    text = LocalTime.of(state.classHour, state.classMinute)
                        .format(DateTimeFormatter.ofPattern("HH:mm")),
                    isPlaceholder = false,
                    onClick = { showTimePicker = true },
                )
                SentenceText(".")
            }

            HorizontalDivider()

            // Trailing clause of the sentence: "…and I want to get reminded 30 min before."
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = state.hasReminder,
                        onCheckedChange = viewModel::onReminderToggle,
                    )
                    Spacer(Modifier.width(12.dp))
                    SentenceText("I want to get reminded")
                }
                if (state.hasReminder) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        SentenceToken(
                            text = "${state.reminderMinutesBefore} min",
                            isPlaceholder = false,
                            onClick = { showReminderPicker = true },
                        )
                        SentenceText("before class.")
                    }
                }
            }

            state.error?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    // Leaves without saving — the caller pops the back stack.
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving,
                ) { Text("Cancel") }
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.weight(1f),
                    enabled = state.canSave && !state.isSaving,
                ) { Text(if (state.isSaving) "Saving…" else "Confirm") }
            }
        }
    }

    if (showTimePicker) {
        ClassTimePickerDialog(
            initialHour = state.classHour,
            initialMinute = state.classMinute,
            onConfirm = { hour, minute ->
                viewModel.onTimeChange(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }

    if (showDayPicker) {
        DayPickerDialog(
            selected = state.selectedDays,
            onToggle = viewModel::toggleDay,
            onDismiss = { showDayPicker = false },
        )
    }

    if (showReminderPicker) {
        ReminderPickerDialog(
            selected = state.reminderMinutesBefore,
            onSelect = {
                viewModel.onReminderMinutesChange(it)
                showReminderPicker = false
            },
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
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )
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
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
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
