package com.abrarshakhi.selfattention.core.model

import java.time.LocalDate
import java.time.LocalDateTime

data class ScheduledClass(
    val course: Course,
    val date: LocalDate,
    val record: AttendanceRecord?,
) {
    val status: AttendanceStatus? get() = record?.status
    val startsAt: LocalDateTime get() = date.atTime(course.classTime)
    val endsAt: LocalDateTime get() = startsAt.plusMinutes(course.classDurationMinutes.toLong())
}
