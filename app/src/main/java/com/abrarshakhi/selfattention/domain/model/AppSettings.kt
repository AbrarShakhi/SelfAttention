package com.abrarshakhi.selfattention.domain.model

import java.time.DayOfWeek

data class AppSettings(
    val weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    val weeklyHolidays: Set<DayOfWeek> = setOf(DayOfWeek.SUNDAY),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appFont: AppFont = AppFont.Default,
    /** False until the user finishes or skips onboarding; decides the start route. */
    val hasCompletedOnboarding: Boolean = false,
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/**
 * The typeface the whole app renders in.
 *
 * [label] doubles as the Google Fonts family name for every entry except [SYSTEM], which uses the
 * platform font and needs no download — the one option guaranteed to work without Play Services
 * or a network.
 */
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
        /** The brand font. */
        val Default = PLUS_JAKARTA_SANS
    }
}
