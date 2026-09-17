package com.abrarshakhi.selfattention.presentation.components

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Week-start aware date maths for the calendars.
 *
 * Both calendars used to assume Monday, which ignored the user's "week starts on" setting.
 */

/** The seven weekdays in display order, starting at [weekStart]. */
internal fun weekdayOrder(weekStart: DayOfWeek): List<DayOfWeek> =
    (0L..6L).map { weekStart.plus(it) }

/** The first day of the week containing this date, honouring [weekStart]. */
internal fun LocalDate.startOfWeek(weekStart: DayOfWeek): LocalDate =
    with(TemporalAdjusters.previousOrSame(weekStart))

/** How many blank cells precede the 1st of the month in a grid starting at [weekStart]. */
internal fun leadingBlankCount(firstOfMonth: LocalDate, weekStart: DayOfWeek): Int =
    (firstOfMonth.dayOfWeek.value - weekStart.value + 7) % 7
