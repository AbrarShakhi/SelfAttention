package com.abrarshakhi.selfattention.feature.course.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.designsystem.component.DetailTopAppBar
import com.abrarshakhi.selfattention.core.designsystem.component.SuccessOverlay
import com.abrarshakhi.selfattention.core.ui.permission.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.feature.course.form.CourseForm
import com.abrarshakhi.selfattention.feature.course.form.FormActionBar
import com.abrarshakhi.selfattention.feature.course.form.NotificationWarning
import kotlinx.coroutines.delay

private const val SuccessDisplayMillis = 1_400L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    onDone: () -> Unit,
    viewModel: AddCourseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications = rememberNotificationPermissionState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            delay(SuccessDisplayMillis)
            onDone()
        }
    }

    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                DetailTopAppBar(
                    title = "New course",
                    subtitle = "Tell us when it meets",
                    onNavigateUp = onDone,
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                FormActionBar(
                    confirmLabel = "Save course",
                    canConfirm = state.form.canSave,
                    isWorking = state.isSaving || state.saved,
                    onConfirm = viewModel::save,
                    onCancel = onDone,
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (!notifications.isGranted) NotificationWarning(notifications)
                CourseForm(
                    state = state.form,
                    onEvent = viewModel::onFormEvent,
                    onReminderEnabled = { if (!notifications.isGranted) notifications.request() },
                )
                state.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        SuccessOverlay(visible = state.saved, animation = R.raw.success_check, message = "Course added")
    }
}
