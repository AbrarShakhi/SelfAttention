package com.abrarshakhi.selfattention.core.alarm

import com.abrarshakhi.selfattention.core.model.Course
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class AlarmTimingTest {

    private val monday = LocalDate.of(2026, 10, 5)

    @Test
    fun `reminder later today is scheduled today`() {
        val course = course(hour = 10, minute = 0, reminder = 15)
        val trigger = nextAlarmTrigger(course, DayOfWeek.MONDAY, AlarmType.PRE_CLASS, monday.atTime(8, 0))

        assertEquals(monday, trigger.classDate)
        assertEquals(monday.atTime(9, 45), trigger.triggerAt)
    }

    @Test
    fun `alarm already passed today moves to next week`() {
        val course = course(hour = 10, minute = 0)
        val trigger = nextAlarmTrigger(course, DayOfWeek.MONDAY, AlarmType.POST_CLASS, monday.atTime(12, 0))

        assertEquals(monday.plusWeeks(1), trigger.classDate)
        assertEquals(monday.plusWeeks(1).atTime(11, 0), trigger.triggerAt)
    }

    @Test
    fun `after-class prompt crossing midnight fires the next morning not before class`() {
        val course = course(hour = 23, minute = 30, duration = 60)
        val trigger = nextAlarmTrigger(course, DayOfWeek.MONDAY, AlarmType.POST_CLASS, monday.atTime(20, 0))

        assertEquals(monday, trigger.classDate)
        assertEquals(monday.plusDays(1).atTime(0, 30), trigger.triggerAt)
    }

    @Test
    fun `after-class prompt still pending just after midnight belongs to yesterday's class`() {
        val course = course(hour = 23, minute = 30, duration = 60)
        val trigger = nextAlarmTrigger(course, DayOfWeek.MONDAY, AlarmType.POST_CLASS, monday.plusDays(1).atTime(0, 10))

        assertEquals(monday, trigger.classDate)
        assertEquals(monday.plusDays(1).atTime(0, 30), trigger.triggerAt)
    }

    @Test
    fun `reminder for an early class fires the evening before`() {
        val course = course(hour = 0, minute = 10, reminder = 30)
        val sundayEvening = monday.minusDays(1).atTime(20, 0)
        val trigger = nextAlarmTrigger(course, DayOfWeek.MONDAY, AlarmType.PRE_CLASS, sundayEvening)

        assertEquals(monday, trigger.classDate)
        assertEquals(monday.minusDays(1).atTime(23, 40), trigger.triggerAt)
    }

    private fun course(hour: Int, minute: Int, duration: Int = 60, reminder: Int = 30) = Course(
        id = 1,
        name = "Course",
        code = "",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = hour,
        classMinute = minute,
        classDurationMinutes = duration,
        hasReminder = true,
        reminderMinutesBefore = reminder,
    )
}
