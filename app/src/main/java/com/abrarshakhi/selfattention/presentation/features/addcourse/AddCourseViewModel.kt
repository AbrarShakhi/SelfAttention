package com.abrarshakhi.selfattention.presentation.features.addcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.usecase.course.AddCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AddCourseViewModel @Inject constructor(
    private val addCourse: AddCourseUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddCourseUiState())
    val state: StateFlow<AddCourseUiState> = _state

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onCodeChange(value: String) = _state.update { it.copy(code = value) }

    fun toggleDay(day: DayOfWeek) = _state.update {
        val days = it.selectedDays.toMutableSet()
        if (days.contains(day)) days.remove(day) else days.add(day)
        it.copy(selectedDays = days)
    }

    fun onTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(classHour = hour, classMinute = minute) }

    fun onDurationChange(minutes: Int) =
        _state.update { it.copy(classDurationMinutes = minutes) }

    fun onReminderToggle(enabled: Boolean) =
        _state.update { it.copy(hasReminder = enabled) }

    fun onReminderMinutesChange(minutes: Int) =
        _state.update { it.copy(reminderMinutesBefore = minutes) }

    fun save() {
        val s = _state.value
        if (!s.canSave) return
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                addCourse(
                    Course(
                        name = s.name.trim(),
                        code = s.code.trim(),
                        scheduleDays = s.selectedDays.sortedBy { it.value },
                        classHour = s.classHour,
                        classMinute = s.classMinute,
                        classDurationMinutes = s.classDurationMinutes,
                        hasReminder = s.hasReminder,
                        reminderMinutesBefore = s.reminderMinutesBefore,
                    )
                )
                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}
