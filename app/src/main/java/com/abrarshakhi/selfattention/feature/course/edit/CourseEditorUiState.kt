package com.abrarshakhi.selfattention.feature.course.edit

import com.abrarshakhi.selfattention.feature.course.form.CourseFormState

data class CourseEditorUiState(
    val courseId: Long = 0,
    val form: CourseFormState = CourseFormState(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)
