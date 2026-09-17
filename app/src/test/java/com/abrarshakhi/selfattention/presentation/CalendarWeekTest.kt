package com.abrarshakhi.selfattention.presentation

import com.abrarshakhi.selfattention.presentation.components.leadingBlankCount
import com.abrarshakhi.selfattention.presentation.components.startOfWeek
import com.abrarshakhi.selfattention.presentation.components.weekdayOrder
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Week-start aware calendar maths. Both calendars previously hardcoded Monday, ignoring the
 * user's "week starts on" setting.
 */
class CalendarWeekTest {

    @Test
    fun `weekday order starts at the configured day and wraps`() {
        assertEquals(
            listOf(
                DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
            ),
            weekdayOrder(DayOfWeek.SUNDAY),
        )
        assertEquals(DayOfWeek.SATURDAY, weekdayOrder(DayOfWeek.SATURDAY).first())
    }

    /** 2026-09-16 is a Wednesday. */
    @Test
    fun `start of week depends on the configured first day`() {
        val wednesday = LocalDate.of(2026, 9, 16)

        assertEquals(LocalDate.of(2026, 9, 14), wednesday.startOfWeek(DayOfWeek.MONDAY))
        assertEquals(LocalDate.of(2026, 9, 13), wednesday.startOfWeek(DayOfWeek.SUNDAY))
        assertEquals(LocalDate.of(2026, 9, 12), wednesday.startOfWeek(DayOfWeek.SATURDAY))
    }

    /** A date that is already the first day of the week must not jump back a week. */
    @Test
    fun `start of week returns the same date when it is already the first day`() {
        val monday = LocalDate.of(2026, 9, 14)

        assertEquals(monday, monday.startOfWeek(DayOfWeek.MONDAY))
    }

    /** 2026-09-01 is a Tuesday, so the grid offset shifts with the configured week start. */
    @Test
    fun `leading blanks shift with the week start`() {
        val firstOfMonth = LocalDate.of(2026, 9, 1)

        assertEquals(1, leadingBlankCount(firstOfMonth, DayOfWeek.MONDAY))
        assertEquals(2, leadingBlankCount(firstOfMonth, DayOfWeek.SUNDAY))
        assertEquals(0, leadingBlankCount(firstOfMonth, DayOfWeek.TUESDAY))
        assertEquals(3, leadingBlankCount(firstOfMonth, DayOfWeek.SATURDAY))
    }
}
