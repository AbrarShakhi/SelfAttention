package com.abrarshakhi.selfattention.feature.course.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.designsystem.component.DetailTopAppBar
import com.abrarshakhi.selfattention.core.designsystem.component.WarningBanner
import com.abrarshakhi.selfattention.core.ui.permission.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.feature.course.form.CourseForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    onDone: () -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications = rememberNotificationPermissionState()

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DetailTopAppBar(
                title = "Add a Course",
                onNavigateUp = onDone,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (!notifications.isGranted) {
                WarningBanner(
                    icon = Icons.Default.NotificationsOff,
                    title = "Notifications are off",
                    message = "This course will be saved, but reminders and attendance prompts " +
                        "won't appear until you allow notifications.",
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
}
