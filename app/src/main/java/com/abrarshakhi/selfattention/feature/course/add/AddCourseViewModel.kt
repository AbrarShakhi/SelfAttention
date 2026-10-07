package com.abrarshakhi.selfattention.feature.course.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.domain.course.AddCourseUseCase
import com.abrarshakhi.selfattention.feature.course.form.CourseFormEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCourseViewModel @Inject constructor(
    private val addCourse: AddCourseUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddCourseUiState())
    val state: StateFlow<AddCourseUiState> = _state.asStateFlow()

    fun onFormEvent(event: CourseFormEvent) {
        _state.update { it.copy(form = it.form.reduce(event), error = null) }
    }

    fun save() {
        val current = _state.value
        if (!current.form.canSave || current.isSaving) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                addCourse(current.form.toNewCourse())
                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Could not save the course") }
            }
        }
    }
}
