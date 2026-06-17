package com.abrarshakhi.selfattention.domain.model

import java.time.DayOfWeek

data class AppSettings(
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = setOf(DayOfWeek.SUNDAY),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
