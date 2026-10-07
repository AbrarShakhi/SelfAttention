package com.abrarshakhi.selfattention.feature.timeline

import com.abrarshakhi.selfattention.core.model.ScheduledClass
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

data class TimelineUiState(
    val selectedDay: LocalDate = LocalDate.now(),
    val schedule: DaySchedule = DaySchedule(LocalDate.now(), emptyList()),
    val visibleMonth: YearMonth = YearMonth.now(),
    val classCountByWeekday: Map<DayOfWeek, Int> = emptyMap(),
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = emptySet(),
    val now: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = true,
) {
    fun classCountOn(date: LocalDate): Int =
        if (date.dayOfWeek in weeklyHolidays) 0 else classCountByWeekday[date.dayOfWeek] ?: 0
}

data class DaySchedule(
    val date: LocalDate,
    val classes: List<ScheduledClass>,
)
