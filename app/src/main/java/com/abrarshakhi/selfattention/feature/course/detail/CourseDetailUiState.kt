package com.abrarshakhi.selfattention.feature.course.detail

import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class CourseDetailUiState(
    val course: Course? = null,
    val stats: CourseStats? = null,
    val records: Map<LocalDate, AttendanceRecord> = emptyMap(),
    val currentMonth: YearMonth = YearMonth.now(),
    val sheetDate: LocalDate? = null,
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = emptySet(),
    val isLoading: Boolean = true,
)
