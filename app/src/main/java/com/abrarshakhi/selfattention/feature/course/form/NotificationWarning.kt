package com.abrarshakhi.selfattention.feature.course.form

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.runtime.Composable
import com.abrarshakhi.selfattention.core.designsystem.component.WarningBanner
import com.abrarshakhi.selfattention.core.ui.permission.PermissionState

@Composable
fun NotificationWarning(state: PermissionState) {
    WarningBanner(
        icon = Icons.Rounded.NotificationsOff,
        title = "Notifications are off",
        message = "The course will be saved, but reminders and attendance prompts won't appear until you allow notifications.",
        actionLabel = if (state.mustUseSettings) "Open settings" else "Allow",
        onAction = state.request,
    )
}
