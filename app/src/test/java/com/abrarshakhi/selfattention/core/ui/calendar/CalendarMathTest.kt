package com.abrarshakhi.selfattention.core.ui.calendar

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

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

    @Test
    fun `start of week depends on the configured first day`() {
        val wednesday = LocalDate.of(2026, 9, 16)

        assertEquals(LocalDate.of(2026, 9, 14), wednesday.startOfWeek(DayOfWeek.MONDAY))
        assertEquals(LocalDate.of(2026, 9, 13), wednesday.startOfWeek(DayOfWeek.SUNDAY))
        assertEquals(LocalDate.of(2026, 9, 12), wednesday.startOfWeek(DayOfWeek.SATURDAY))
    }

    @Test
    fun `start of week returns the same date when it is already the first day`() {
        val monday = LocalDate.of(2026, 9, 14)

        assertEquals(monday, monday.startOfWeek(DayOfWeek.MONDAY))
    }

    @Test
    fun `leading blanks shift with the week start`() {
        val firstOfMonth = LocalDate.of(2026, 9, 1)

        assertEquals(1, leadingBlankCount(firstOfMonth, DayOfWeek.MONDAY))
        assertEquals(2, leadingBlankCount(firstOfMonth, DayOfWeek.SUNDAY))
        assertEquals(0, leadingBlankCount(firstOfMonth, DayOfWeek.TUESDAY))
        assertEquals(3, leadingBlankCount(firstOfMonth, DayOfWeek.SATURDAY))
    }
}
