package com.abrarshakhi.selfattention.feature.course.add

import com.abrarshakhi.selfattention.feature.course.form.CourseFormState

data class AddCourseUiState(
    val form: CourseFormState = CourseFormState(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)
