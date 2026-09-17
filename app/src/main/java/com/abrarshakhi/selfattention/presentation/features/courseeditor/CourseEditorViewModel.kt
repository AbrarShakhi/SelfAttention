package com.abrarshakhi.selfattention.presentation.features.courseeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.usecase.course.DeleteCourseUseCase
import com.abrarshakhi.selfattention.domain.usecase.course.GetCourseByIdUseCase
import com.abrarshakhi.selfattention.domain.usecase.course.UpdateCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class CourseEditorViewModel @Inject constructor(
    private val getCourseById: GetCourseByIdUseCase,
    private val updateCourse: UpdateCourseUseCase,
    private val deleteCourse: DeleteCourseUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseEditorUiState())
    val state: StateFlow<CourseEditorUiState> = _state

    /**
     * The course as stored, kept for two reasons: `copy` preserves `id` and `createdAt` (which
     * drives the stats window and is not editable), and deleting must cancel alarms using the
     * *saved* schedule days, not whatever the user has typed but not committed.
     */
    private var original: Course? = null
    private var loadStarted = false

    /** Idempotent: re-entering the screen must not discard edits already in progress. */
    fun load(courseId: Long) {
        if (loadStarted) return
        loadStarted = true
        viewModelScope.launch {
            val course = getCourseById(courseId)
            if (course == null) {
                // Deleted from somewhere else while this screen was open; nothing left to edit.
                _state.update { it.copy(isLoading = false, deleted = true) }
                return@launch
            }
            original = course
            _state.update {
                it.copy(
                    name = course.name,
                    code = course.code,
                    selectedDays = course.scheduleDays.toSet(),
                    classHour = course.classHour,
                    classMinute = course.classMinute,
                    hasReminder = course.hasReminder,
                    reminderMinutesBefore = course.reminderMinutesBefore,
                    isLoading = false,
                )
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onCodeChange(value: String) = _state.update { it.copy(code = value) }

    fun toggleDay(day: DayOfWeek) = _state.update {
        val days = it.selectedDays.toMutableSet()
        if (!days.add(day)) days.remove(day)
        it.copy(selectedDays = days)
    }

    fun onTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(classHour = hour, classMinute = minute) }

    fun onReminderToggle(enabled: Boolean) = _state.update { it.copy(hasReminder = enabled) }

    fun onReminderMinutesChange(minutes: Int) =
        _state.update { it.copy(reminderMinutesBefore = minutes) }

    fun save() {
        val current = original ?: return
        val s = _state.value
        if (!s.canSave || s.isSaving) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                // copy() keeps id and createdAt; UpdateCourseUseCase re-arms the alarms.
                updateCourse(
                    current.copy(
                        name = s.name.trim(),
                        code = s.code.trim(),
                        scheduleDays = s.selectedDays.sortedBy { day -> day.value },
                        classHour = s.classHour,
                        classMinute = s.classMinute,
                        hasReminder = s.hasReminder,
                        reminderMinutesBefore = s.reminderMinutesBefore,
                    ),
                )
                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Could not save") }
            }
        }
    }

    fun delete() {
        val current = original ?: return
        if (_state.value.isSaving) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                // Deliberately the stored course: cancelling alarms depends on the saved days.
                deleteCourse(current)
                _state.update { it.copy(isSaving = false, deleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Could not delete") }
            }
        }
    }
}
