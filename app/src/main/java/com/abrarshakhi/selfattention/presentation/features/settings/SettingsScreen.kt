package com.abrarshakhi.selfattention.presentation.features.settings

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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.presentation.components.FontSelector
import com.abrarshakhi.selfattention.presentation.components.ThemeModeSelector
import com.abrarshakhi.selfattention.presentation.components.WeekStartSelector
import com.abrarshakhi.selfattention.presentation.components.WeeklyHolidaySelector
import com.abrarshakhi.selfattention.presentation.components.WarningBanner
import com.abrarshakhi.selfattention.presentation.permissions.rememberExactAlarmPermissionState
import com.abrarshakhi.selfattention.presentation.permissions.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import com.abrarshakhi.selfattention.presentation.theme.fontFamilyFor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsContent(
        settings = state.settings,
        backupMessage = state.backupMessage,
        onExport = viewModel::exportTo,
        onImport = viewModel::importFrom,
        onDismissBackupMessage = viewModel::dismissBackupMessage,
        onThemeModeChange = viewModel::setThemeMode,
        onAppFontChange = viewModel::setAppFont,
        onWeekStartDayChange = viewModel::setWeekStartDay,
        onHolidayToggle = viewModel::toggleHoliday,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingsContent(
    settings: AppSettings,
    backupMessage: BackupMessage?,
    onExport: (Uri) -> Unit,
    onImport: (Uri) -> Unit,
    onDismissBackupMessage: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAppFontChange: (AppFont) -> Unit,
    onWeekStartDayChange: (DayOfWeek) -> Unit,
    onHolidayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale

    // The app's only Scaffold lives in AppRoot, so this screen is content-only. Surface gives it
    // the themed background and is the semantic container the rest of the screen sits on.
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            // ── Reminders ────────────────────────────────────────────────────
            SettingsHeader("Reminders")
            ReminderPermissions(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsDivider()

            // ── Appearance ───────────────────────────────────────────────────
            SettingsHeader("Appearance")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeModeSelector(selected = settings.themeMode, onSelect = onThemeModeChange)
                Text(
                    text = "System follows your device's light or dark setting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            FontSelector(selected = settings.appFont, onSelect = onAppFontChange)

            SettingsDivider()

            // ── Week ─────────────────────────────────────────────────────────
            SettingsHeader("Week")
            WeekStartSelector(
                selected = settings.weekStartDay,
                onSelect = onWeekStartDayChange,
            )

            SettingsDivider()

            // ── Weekly holidays ──────────────────────────────────────────────
            SettingsHeader("Weekly holidays")
            WeeklyHolidaySelector(
                selected = settings.weeklyHolidays,
                onToggle = onHolidayToggle,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            SettingsDivider()

            SettingsHeader("Backup")
            BackupActions(
                onExport = onExport,
                onImport = onImport,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    if (backupMessage != null) {
        AlertDialog(
            onDismissRequest = onDismissBackupMessage,
            icon = {
                Icon(
                    imageVector = if (backupMessage.isError) {
                        Icons.Default.ErrorOutline
                    } else {
                        Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (backupMessage.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        AppTheme.status.present.color
                    },
                )
            },
            title = { Text(backupMessage.title) },
            text = { Text(backupMessage.body) },
            confirmButton = {
                TextButton(onClick = onDismissBackupMessage) { Text("OK") }
            },
        )
    }


}

/**
 * Shows whether the reminder pipeline can actually run.
 *
 * Both permissions fail silently in the scheduler and notifier, so this is the only place the app
 * tells the user that reminders are switched off at the system level.
 */
@Composable
private fun ReminderPermissions(modifier: Modifier = Modifier) {
    val notifications = rememberNotificationPermissionState()
    val exactAlarms = rememberExactAlarmPermissionState()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!notifications.isGranted) {
            WarningBanner(
                icon = Icons.Default.NotificationsOff,
                title = "Notifications are off",
                message = "Reminders and \"did you attend?\" prompts cannot appear until you " +
                    "allow notifications for this app.",
                actionLabel = if (notifications.mustUseSettings) "Open settings" else "Allow",
                onAction = notifications.request,
            )
        }

        if (!exactAlarms.isGranted) {
            WarningBanner(
                icon = Icons.Default.AlarmOff,
                title = "Exact alarms are off",
                message = "Reminders can't be scheduled at the right time without permission to " +
                    "set exact alarms.",
                actionLabel = "Open settings",
                onAction = exactAlarms.request,
            )
        }

        if (notifications.isGranted && exactAlarms.isGranted) {
            ListItem(
                headlineContent = { Text("Reminders are set up") },
                supportingContent = { Text("Notifications and exact alarms are allowed.") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppTheme.status.present.color,
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

/**
 * Export writes a file through the Storage Access Framework, so no storage permission is needed
 * and the user picks where it goes. Import is additive — it never replaces existing courses.
 */
@Composable
private fun BackupActions(
    onExport: (Uri) -> Unit,
    onImport: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let(onExport) }

    // "*/*" as well: some file pickers hide .json files when the filter is strict, and an
    // unreadable pick is handled with a message anyway.
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(onImport) }

    val defaultName = remember {
        "self-attention-backup-${LocalDate.now().format(DateTimeFormatter.ISO_DATE)}.json"
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { exportLauncher.launch(defaultName) },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Export")
            }
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Import")
            }
        }
        Text(
            text = "Export saves every course and attendance record as a JSON file. " +
                "Importing adds those courses to your existing ones.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}



@Composable
private fun SettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .semantics { heading() },
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
}


