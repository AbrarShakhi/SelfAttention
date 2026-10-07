package com.abrarshakhi.selfattention.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessageDialog
import com.abrarshakhi.selfattention.core.ui.permission.rememberExactAlarmPermissionState
import com.abrarshakhi.selfattention.core.ui.permission.rememberNotificationPermissionState
import com.abrarshakhi.selfattention.feature.settings.component.AboutSection
import com.abrarshakhi.selfattention.feature.settings.component.AppearanceSection
import com.abrarshakhi.selfattention.feature.settings.component.BackupSection
import com.abrarshakhi.selfattention.feature.settings.component.RemindersSection
import com.abrarshakhi.selfattention.feature.settings.component.WeekSection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Settings") },
                subtitle = { Text("Make it yours") },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RemindersSection(
                notifications = rememberNotificationPermissionState(),
                exactAlarms = rememberExactAlarmPermissionState(),
            )
            AppearanceSection(
                settings = state.settings,
                onThemeModeChange = viewModel::setThemeMode,
                onUseWallpaperColorsChange = viewModel::setUseWallpaperColors,
                onSeedColorChange = viewModel::setSeedColor,
                onColorStyleChange = viewModel::setColorStyle,
                onPureBlackChange = viewModel::setPureBlack,
                onAppFontChange = viewModel::setAppFont,
            )
            WeekSection(
                settings = state.settings,
                onWeekStartChange = viewModel::setWeekStartDay,
                onHolidayToggle = viewModel::toggleHoliday,
            )
            BackupSection(onExport = viewModel::exportTo, onImport = viewModel::importFrom)
            AboutSection()
            Spacer(Modifier.height(24.dp))
        }
    }

    state.backupMessage?.let { BackupMessageDialog(message = it, onDismiss = viewModel::dismissBackupMessage) }
}
