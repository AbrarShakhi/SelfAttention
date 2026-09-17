package com.abrarshakhi.selfattention.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
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

/**
 * Whether this course actually meets on [date].
 *
 * A weekly holiday means no classes happen that weekday at all, so it overrides the schedule.
 * This only affects days the course *would* meet from now on — attendance already recorded on a
 * weekday later marked a holiday is history and still counts.
 */
fun Course.meetsOn(date: LocalDate, weeklyHolidays: Set<DayOfWeek>): Boolean =
    date.dayOfWeek in scheduleDays && date.dayOfWeek !in weeklyHolidays
