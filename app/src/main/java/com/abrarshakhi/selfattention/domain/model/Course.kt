package com.abrarshakhi.selfattention.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Course(
    val id: Long = 0,
    val name: String,
    val code: String,
    val scheduleDays: List<DayOfWeek>,
    val classHour: Int,
    val classMinute: Int,
    val classDurationMinutes: Int = 60,
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 30,
    val createdAt: Long = System.currentTimeMillis(),
) {
    val classTime: LocalTime get() = LocalTime.of(classHour, classMinute)
}
