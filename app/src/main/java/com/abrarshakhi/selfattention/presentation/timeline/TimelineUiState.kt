package com.abrarshakhi.selfattention.presentation.timeline

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Subject
import java.time.LocalDate

data class TimelineUiState(
    val weekDays: List<LocalDate> = emptyList(),
    val selectedDay: LocalDate = LocalDate.now(),
    val classesForDay: List<ScheduledClass> = emptyList(),
)

data class ScheduledClass(
    val subject: Subject,
    val date: LocalDate,
    val record: AttendanceRecord?,
)
