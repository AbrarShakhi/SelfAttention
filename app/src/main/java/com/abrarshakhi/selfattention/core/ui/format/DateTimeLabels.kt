package com.abrarshakhi.selfattention.core.ui.format

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val TimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun LocalTime.clockLabel(): String = format(TimeFormat)

fun LocalDateTime.clockLabel(): String = format(TimeFormat)

fun LocalDate.longLabel(locale: Locale): String =
    format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))

fun LocalDate.relativeDayLabel(today: LocalDate, locale: Locale): String = when (this) {
    today -> "Today"
    today.plusDays(1) -> "Tomorrow"
    else -> dayOfWeek.getDisplayName(TextStyle.FULL, locale)
}

fun countdownLabel(from: LocalDateTime, to: LocalDateTime): String {
    val minutes = Duration.between(from, to).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "Starting now"
        minutes < 60 -> "In $minutes min"
        minutes < 24 * 60 -> "In ${minutes / 60} h ${minutes % 60} min"
        else -> "In ${minutes / (24 * 60)} days"
    }
}

fun greetingFor(time: LocalTime): String = when (time.hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..21 -> "Good evening"
    else -> "Good night"
}
