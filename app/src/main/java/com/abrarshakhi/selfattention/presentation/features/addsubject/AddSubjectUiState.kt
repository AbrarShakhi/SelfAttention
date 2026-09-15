package com.abrarshakhi.selfattention.presentation.addsubject

import java.time.DayOfWeek

data class AddSubjectUiState(
    val name: String = "",
    val code: String = "",
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val classHour: Int = 9,
    val classMinute: Int = 0,
    val classDurationMinutes: Int = 60,
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 30,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && selectedDays.isNotEmpty()
}
