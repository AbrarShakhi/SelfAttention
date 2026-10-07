package com.abrarshakhi.selfattention.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.backup.ImportBackupUseCase
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessage
import com.abrarshakhi.selfattention.core.ui.backup.toBackupMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val importBackup: ImportBackupUseCase,
) : ViewModel() {

    private val backupMessage = MutableStateFlow<BackupMessage?>(null)

    val state: StateFlow<OnboardingUiState> =
        combine(settingsRepository.getSettings(), backupMessage, ::OnboardingUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OnboardingUiState())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setAppFont(font: AppFont) {
        viewModelScope.launch { settingsRepository.setAppFont(font) }
    }

    fun setWeekStartDay(day: DayOfWeek) {
        viewModelScope.launch { settingsRepository.setWeekStartDay(day) }
    }

    fun toggleHoliday(day: DayOfWeek) {
        viewModelScope.launch { settingsRepository.toggleWeeklyHoliday(day) }
    }

    fun importFrom(uri: String) {
        viewModelScope.launch { backupMessage.value = importBackup(uri).toBackupMessage() }
    }

    fun dismissBackupMessage() {
        backupMessage.value = null
    }

    fun completeOnboarding() {
        viewModelScope.launch { settingsRepository.setOnboardingComplete() }
    }
}
