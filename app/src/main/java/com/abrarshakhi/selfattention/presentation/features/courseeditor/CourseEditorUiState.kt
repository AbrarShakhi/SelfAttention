package com.abrarshakhi.selfattention.presentation.features.courseeditor

import java.time.DayOfWeek

data class CourseEditorUiState(
    val name: String = "",
    val code: String = "",
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val classHour: Int = 9,
    val classMinute: Int = 0,
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 30,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && selectedDays.isNotEmpty()
}
