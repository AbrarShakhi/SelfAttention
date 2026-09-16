package com.abrarshakhi.selfattention.presentation.features.settings

import com.abrarshakhi.selfattention.domain.model.AppSettings

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
)
