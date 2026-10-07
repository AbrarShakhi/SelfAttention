package com.abrarshakhi.selfattention.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.backup.ExportBackupUseCase
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
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exportBackup: ExportBackupUseCase,
    private val importBackup: ImportBackupUseCase,
) : ViewModel() {

    private val backupMessage = MutableStateFlow<BackupMessage?>(null)

    val state: StateFlow<SettingsUiState> =
        combine(settingsRepository.getSettings(), backupMessage, ::SettingsUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setWeekStartDay(day: DayOfWeek) {
        viewModelScope.launch { settingsRepository.setWeekStartDay(day) }
    }

    fun toggleHoliday(day: DayOfWeek) {
        viewModelScope.launch { settingsRepository.toggleWeeklyHoliday(day) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setAppFont(font: AppFont) {
        viewModelScope.launch { settingsRepository.setAppFont(font) }
    }

    fun exportTo(uri: String) {
        viewModelScope.launch { backupMessage.value = exportBackup(uri).toBackupMessage() }
    }

    fun importFrom(uri: String) {
        viewModelScope.launch { backupMessage.value = importBackup(uri).toBackupMessage() }
    }

    fun dismissBackupMessage() {
        backupMessage.value = null
    }
}
