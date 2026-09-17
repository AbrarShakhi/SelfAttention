package com.abrarshakhi.selfattention.domain

import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.meetsOn
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * [meetsOn] is the single rule deciding whether a course actually has a class on a date.
 * A weekly holiday overrides the schedule.
 */
class CourseScheduleTest {

    /** 2026-09-14 is a Monday, 2026-09-16 a Wednesday, 2026-09-18 a Friday. */
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

    /** The headline behaviour: a weekly holiday wins over the course's own schedule. */
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

    // ── helpers ──────────────────────────────────────────────────────────────

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
