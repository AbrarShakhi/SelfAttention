package com.abrarshakhi.selfattention.presentation.features.courseeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.presentation.components.CourseForm
import com.abrarshakhi.selfattention.presentation.components.WarningBanner
import com.abrarshakhi.selfattention.presentation.permissions.rememberNotificationPermissionState

/**
 * Edit or delete an existing course.
 *
 * @param onSaved pop back to the course, which re-reads and shows the new values.
 * @param onDeleted pop past the course too — its detail screen no longer has anything to show.
 */
@Composable
fun CourseEditorScreen(
    courseId: Long,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: CourseEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications = rememberNotificationPermissionState()
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.load(courseId) }
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Surface
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (state.hasReminder && !notifications.isGranted) {
                WarningBanner(
                    icon = Icons.Default.NotificationsOff,
                    title = "Notifications are off",
                    message = "Changes will be saved, but reminders won't appear until you allow " +
                        "notifications.",
                    actionLabel = if (notifications.mustUseSettings) "Open settings" else "Allow",
                    onAction = notifications.request,
                )
            }

            CourseForm(
                name = state.name,
                code = state.code,
                selectedDays = state.selectedDays,
                classHour = state.classHour,
                classMinute = state.classMinute,
                hasReminder = state.hasReminder,
                reminderMinutesBefore = state.reminderMinutesBefore,
                onNameChange = viewModel::onNameChange,
                onCodeChange = viewModel::onCodeChange,
                onToggleDay = viewModel::toggleDay,
                onTimeChange = viewModel::onTimeChange,
                onReminderToggle = { enabled ->
                    viewModel.onReminderToggle(enabled)
                    if (enabled && !notifications.isGranted) notifications.request()
                },
                onReminderMinutesChange = viewModel::onReminderMinutesChange,
            )

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
                    onClick = onSaved,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving,
                ) { Text("Cancel") }
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.weight(1f),
                    enabled = state.canSave && !state.isSaving,
                ) { Text(if (state.isSaving) "Saving…" else "Save") }
            }

            OutlinedButton(
                onClick = { confirmDelete = true },
                enabled = !state.isSaving,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("  Delete course")
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete this course?") },
            text = {
                Text(
                    "Its attendance history will be deleted too and its reminders cancelled. " +
                        "This can't be undone.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { confirmDelete = false; viewModel.delete() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            },
        )
    }
}
