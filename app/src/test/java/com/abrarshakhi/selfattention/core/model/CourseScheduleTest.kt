package com.abrarshakhi.selfattention.core.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class CourseScheduleTest {

    private val monday = LocalDate.of(2026, 9, 14)
    private val wednesday = LocalDate.of(2026, 9, 16)
    private val friday = LocalDate.of(2026, 9, 18)

    @Test
    fun `meets on a scheduled weekday`() {
        val course = buildCourse(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)

        assertTrue(course.meetsOn(monday, weeklyHolidays = emptySet()))
        assertTrue(course.meetsOn(friday, weeklyHolidays = emptySet()))
    }

    @Test
    fun `does not meet on an unscheduled weekday`() {
        val course = buildCourse(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)

        assertFalse(course.meetsOn(wednesday, weeklyHolidays = emptySet()))
    }

    @Test
    fun `a weekly holiday overrides the schedule`() {
        val course = buildCourse(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)

        assertFalse(course.meetsOn(friday, weeklyHolidays = setOf(DayOfWeek.FRIDAY)))
        assertTrue(course.meetsOn(monday, weeklyHolidays = setOf(DayOfWeek.FRIDAY)))
    }

    @Test
    fun `a holiday on an unscheduled day changes nothing`() {
        val course = buildCourse(DayOfWeek.MONDAY)

        assertFalse(course.meetsOn(wednesday, weeklyHolidays = setOf(DayOfWeek.WEDNESDAY)))
        assertTrue(course.meetsOn(monday, weeklyHolidays = setOf(DayOfWeek.WEDNESDAY)))
    }

    private fun buildCourse(vararg days: DayOfWeek) = Course(
        id = 1L,
        name = "Databases",
        code = "CS-201",
        scheduleDays = days.toList(),
        classHour = 9,
        classMinute = 30,
        classDurationMinutes = 60,
        hasReminder = false,
        reminderMinutesBefore = 30,
        createdAt = 1_758_000_000_000L,
    )
}
