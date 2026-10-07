package com.abrarshakhi.selfattention.feature.onboarding

import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessage

data class OnboardingUiState(
    val settings: AppSettings = AppSettings(),
    val backupMessage: BackupMessage? = null,
)
