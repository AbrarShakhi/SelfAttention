package com.abrarshakhi.selfattention.presentation.features.timeline

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Course
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class TimelineUiState(
    val selectedDay: LocalDate = LocalDate.now(),
    val visibleMonth: YearMonth = YearMonth.now(),
    val classesForDay: List<ScheduledClass> = emptyList(),
    val classCountByWeekday: Map<DayOfWeek, Int> = emptyMap(),
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = emptySet(),
    val isLoading: Boolean = true,
) {
    /** Weekly holidays mean no classes that weekday, so they never carry an indicator dot. */
    fun classCountOn(date: LocalDate): Int =
        if (date.dayOfWeek in weeklyHolidays) 0 else classCountByWeekday[date.dayOfWeek] ?: 0

    val selectedDayIsHoliday: Boolean get() = selectedDay.dayOfWeek in weeklyHolidays
}

data class ScheduledClass(
    val course: Course,
    val date: LocalDate,
    val record: AttendanceRecord?,
)
