package com.abrarshakhi.selfattention.core.model

import java.time.DayOfWeek

data class AppSettings(
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = setOf(DayOfWeek.SUNDAY),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appFont: AppFont = AppFont.Default,
    val colorPreferences: ColorPreferences = ColorPreferences(),
    val hasCompletedOnboarding: Boolean = false,
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class AppFont(val label: String) {
    PLUS_JAKARTA_SANS("Plus Jakarta Sans"),
    INTER("Inter"),
    OUTFIT("Outfit"),
    NUNITO("Nunito"),
    SPACE_GROTESK("Space Grotesk"),
    LORA("Lora"),
    CAVEAT("Caveat"),
    SYSTEM("System default"),
    ;

    val isDownloadable: Boolean get() = this != SYSTEM

    companion object {
        val Default = PLUS_JAKARTA_SANS
    }
}
