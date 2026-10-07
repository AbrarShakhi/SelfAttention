package com.abrarshakhi.selfattention.core.model

import java.time.LocalDate

data class AttendanceRecord(
    val id: Long = 0,
    val courseId: Long,
    val date: LocalDate,
    val status: AttendanceStatus,
)
