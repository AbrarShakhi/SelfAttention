package com.abrarshakhi.selfattention.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.designsystem.component.WarningBanner
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessage
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessageDialog
import com.abrarshakhi.selfattention.core.ui.permission.rememberExactAlarmPermissionState
import com.abrarshakhi.selfattention.core.ui.permission.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.core.ui.preference.FontSelector
import com.abrarshakhi.selfattention.core.ui.preference.ThemeModeSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeekStartSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeeklyHolidaySelector
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    settings: AppSettings,
    backupMessage: BackupMessage?,
    onExport: (String) -> Unit,
    onImport: (String) -> Unit,
    onDismissBackupMessage: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAppFontChange: (AppFont) -> Unit,
    onWeekStartDayChange: (DayOfWeek) -> Unit,
    onHolidayToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {

            SettingsHeader("Reminders")
            ReminderPermissions(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsDivider()

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

            SettingsHeader("Week")
            WeekStartSelector(
                selected = settings.weekStartDay,
                onSelect = onWeekStartDayChange,
            )

            SettingsDivider()

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

    backupMessage?.let { BackupMessageDialog(message = it, onDismiss = onDismissBackupMessage) }
}

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

@Composable
private fun BackupActions(
    onExport: (String) -> Unit,
    onImport: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let { onExport(it.toString()) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let { onImport(it.toString()) } }

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


