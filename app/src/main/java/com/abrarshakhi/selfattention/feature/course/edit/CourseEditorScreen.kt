package com.abrarshakhi.selfattention.feature.course.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.designsystem.component.DetailTopAppBar
import com.abrarshakhi.selfattention.core.designsystem.component.LoadingContent
import com.abrarshakhi.selfattention.core.ui.permission.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.feature.course.form.CourseForm
import com.abrarshakhi.selfattention.feature.course.form.FormActionBar
import com.abrarshakhi.selfattention.feature.course.form.NotificationWarning

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CourseEditorScreen(
    courseId: Long,
    onNavigateUp: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: CourseEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications = rememberNotificationPermissionState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.load(courseId) }
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DetailTopAppBar(
                title = "Edit course",
                subtitle = state.form.name.takeIf { it.isNotBlank() },
                onNavigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = { confirmDelete = true },
                        enabled = !state.isLoading && !state.isSaving,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete course")
                    }
                },
            )
        },
        bottomBar = {
            if (!state.isLoading) {
                FormActionBar(
                    confirmLabel = "Save changes",
                    canConfirm = state.form.canSave,
                    isWorking = state.isSaving,
                    onConfirm = viewModel::save,
                    onCancel = onNavigateUp,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingContent(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.form.hasReminder && !notifications.isGranted) NotificationWarning(notifications)
            CourseForm(
                state = state.form,
                onEvent = viewModel::onFormEvent,
                onReminderEnabled = { if (!notifications.isGranted) notifications.request() },
                courseId = state.courseId,
            )
            state.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            icon = { Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete this course?") },
            text = { Text("Its attendance history will be deleted and its reminders cancelled. This can't be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        confirmDelete = false
                        viewModel.delete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Keep") } },
        )
    }
}
