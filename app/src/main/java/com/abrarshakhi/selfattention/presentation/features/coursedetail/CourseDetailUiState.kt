package com.abrarshakhi.selfattention.presentation.features.coursedetail

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats
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
