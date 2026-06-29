package com.abrarshakhi.selfattention.presentation.subjectdetail

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import java.time.LocalDate
import java.time.YearMonth

data class SubjectDetailUiState(
    val subject: Subject? = null,
    val stats: SubjectStats? = null,
    val records: Map<LocalDate, AttendanceRecord> = emptyMap(),
    val currentMonth: YearMonth = YearMonth.now(),
    val sheetDate: LocalDate? = null,
    val isLoading: Boolean = true,
)
