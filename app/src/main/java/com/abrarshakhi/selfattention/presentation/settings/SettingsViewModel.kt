package com.abrarshakhi.selfattention.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

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
}
