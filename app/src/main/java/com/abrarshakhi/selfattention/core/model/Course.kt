package com.abrarshakhi.selfattention.core.model

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

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

    fun createdOn(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(createdAt).atZone(zone).toLocalDate()
}

fun Course.meetsOn(date: LocalDate, weeklyHolidays: Set<DayOfWeek>): Boolean =
    date.dayOfWeek in scheduleDays && date.dayOfWeek !in weeklyHolidays
