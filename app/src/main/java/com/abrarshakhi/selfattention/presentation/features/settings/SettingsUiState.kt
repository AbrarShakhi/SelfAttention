package com.abrarshakhi.selfattention.presentation.features.settings

import com.abrarshakhi.selfattention.domain.model.AppSettings

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
    /** Outcome of the last import or export, shown in a dialog until dismissed. */
    val backupMessage: BackupMessage? = null,
)

data class BackupMessage(val title: String, val body: String, val isError: Boolean)
