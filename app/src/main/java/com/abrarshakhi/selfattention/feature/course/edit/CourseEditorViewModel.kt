package com.abrarshakhi.selfattention.feature.course.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.domain.course.DeleteCourseUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCourseByIdUseCase
import com.abrarshakhi.selfattention.core.domain.course.UpdateCourseUseCase
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.feature.course.form.CourseFormEvent
import com.abrarshakhi.selfattention.feature.course.form.CourseFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseEditorViewModel @Inject constructor(
    private val getCourseById: GetCourseByIdUseCase,
    private val updateCourse: UpdateCourseUseCase,
    private val deleteCourse: DeleteCourseUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseEditorUiState())
    val state: StateFlow<CourseEditorUiState> = _state.asStateFlow()

    private var original: Course? = null

    fun load(courseId: Long) {
        if (original?.id == courseId) return
        viewModelScope.launch {
            val course = getCourseById(courseId)
            if (course == null) {
                _state.update { it.copy(isLoading = false, deleted = true) }
                return@launch
            }
            original = course
            _state.update {
                it.copy(courseId = course.id, form = CourseFormState.from(course), isLoading = false)
            }
        }
    }

    fun onFormEvent(event: CourseFormEvent) {
        _state.update { it.copy(form = it.form.reduce(event), error = null) }
    }

    fun save() {
        val course = original ?: return
        val current = _state.value
        if (!current.form.canSave || current.isSaving) return
        perform(onSuccess = { it.copy(saved = true) }) { updateCourse(current.form.applyTo(course)) }
    }

    fun delete() {
        val course = original ?: return
        if (_state.value.isSaving) return
        perform(onSuccess = { it.copy(deleted = true) }) { deleteCourse(course) }
    }

    private fun perform(
        onSuccess: (CourseEditorUiState) -> CourseEditorUiState,
        action: suspend () -> Unit,
    ) {
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                action()
                _state.update { onSuccess(it.copy(isSaving = false)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Something went wrong") }
            }
        }
    }
}
