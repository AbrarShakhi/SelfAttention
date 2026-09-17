package com.abrarshakhi.selfattention.presentation.features.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.data.backup.BackupFileStore
import com.abrarshakhi.selfattention.domain.backup.ImportResult
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import com.abrarshakhi.selfattention.domain.usecase.backup.ExportBackupUseCase
import com.abrarshakhi.selfattention.domain.usecase.backup.ImportBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exportBackup: ExportBackupUseCase,
    private val importBackup: ImportBackupUseCase,
    private val fileStore: BackupFileStore,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()
    private val _isReady = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _state.update { it.copy(settings = settings, isLoading = false) }
            }
        }
    }

    fun setWeekStartDay(day: DayOfWeek) {
        viewModelScope.launch { settingsRepository.setWeekStartDay(day) }
    }

    fun toggleHoliday(day: DayOfWeek) {
        val current = _state.value.settings.weeklyHolidays.toMutableSet()
        if (current.contains(day)) current.remove(day) else current.add(day)
        viewModelScope.launch { settingsRepository.setWeeklyHolidays(current) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setAppFont(font: AppFont) {
        viewModelScope.launch { settingsRepository.setAppFont(font) }
    }

    fun exportTo(uri: Uri) {
        viewModelScope.launch {
            val message = try {
                fileStore.write(uri, exportBackup())
                BackupMessage(
                    title = "Backup saved",
                    body = "Your courses and attendance have been written to the file you chose.",
                    isError = false,
                )
            } catch (e: Exception) {
                BackupMessage(
                    title = "Export failed",
                    body = e.message ?: "The file could not be written.",
                    isError = true,
                )
            }
            _state.update { it.copy(backupMessage = message) }
        }
    }

    fun importFrom(uri: Uri) {
        viewModelScope.launch {
            val message = try {
                when (val result = importBackup(fileStore.read(uri))) {
                    is ImportResult.Success -> BackupMessage(
                        title = "Import complete",
                        body = "Added ${result.courses} course(s) and " +
                            "${result.attendance} attendance record(s).",
                        isError = false,
                    )

                    is ImportResult.Failure -> BackupMessage(
                        title = "Couldn't import that file",
                        body = result.message,
                        isError = true,
                    )
                }
            } catch (e: Exception) {
                // Reading the file itself failed — a revoked Uri, say. Never surfaces as a crash.
                BackupMessage(
                    title = "Couldn't read that file",
                    body = e.message ?: "The file could not be opened.",
                    isError = true,
                )
            }
            _state.update { it.copy(backupMessage = message) }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch { settingsRepository.setOnboardingComplete() }
    }

    fun dismissBackupMessage() = _state.update { it.copy(backupMessage = null) }
}
