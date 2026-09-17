package com.abrarshakhi.selfattention.presentation.features.timeline

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Subject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class TimelineUiState(
    val selectedDay: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.now(),
    val classesForDay: List<ScheduledClass> = emptyList(),
    val classCountByWeekday: Map<DayOfWeek, Int> = emptyMap(),
    val isLoading: Boolean = true,
) {
    fun classCountOn(date: LocalDate): Int = classCountByWeekday[date.dayOfWeek] ?: 0
}

data class ScheduledClass(
    val subject: Subject,
    val date: LocalDate,
    val record: AttendanceRecord?,
)
