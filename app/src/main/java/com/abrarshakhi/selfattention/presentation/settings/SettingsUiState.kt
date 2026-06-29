package com.abrarshakhi.selfattention.presentation.settings

import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import java.time.DayOfWeek

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
)
