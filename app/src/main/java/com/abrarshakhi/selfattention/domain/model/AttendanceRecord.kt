package com.abrarshakhi.selfattention.domain.model

import java.time.LocalDate

data class AttendanceRecord(
    val id: Long = 0,
    val subjectId: Long,
    val date: LocalDate,
    val status: AttendanceStatus,
)
