package com.abrarshakhi.selfattention.core.ui.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal fun weekdayOrder(weekStart: DayOfWeek): List<DayOfWeek> =
    (0L..6L).map { weekStart.plus(it) }

internal fun LocalDate.startOfWeek(weekStart: DayOfWeek): LocalDate =
    with(TemporalAdjusters.previousOrSame(weekStart))

internal fun leadingBlankCount(firstOfMonth: LocalDate, weekStart: DayOfWeek): Int =
    (firstOfMonth.dayOfWeek.value - weekStart.value + 7) % 7
