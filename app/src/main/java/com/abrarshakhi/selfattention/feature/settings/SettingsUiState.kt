package com.abrarshakhi.selfattention.feature.settings

import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessage

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val backupMessage: BackupMessage? = null,
)
