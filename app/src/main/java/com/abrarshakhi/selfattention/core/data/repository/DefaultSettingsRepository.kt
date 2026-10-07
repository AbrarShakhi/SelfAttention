package com.abrarshakhi.selfattention.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.ColorPreferences
import com.abrarshakhi.selfattention.core.model.ColorStyle
import com.abrarshakhi.selfattention.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject

class DefaultSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private object Keys {
        val WEEK_START_DAY = intPreferencesKey("week_start_day")
        val WEEKLY_HOLIDAYS = stringSetPreferencesKey("weekly_holidays")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_FONT = stringPreferencesKey("app_font")
        val WALLPAPER_COLORS = booleanPreferencesKey("use_wallpaper_colors")
        val SEED_COLOR = intPreferencesKey("seed_color")
        val COLOR_STYLE = stringPreferencesKey("color_style")
        val PURE_BLACK = booleanPreferencesKey("pure_black")
        val ONBOARDED = booleanPreferencesKey("has_completed_onboarding")
    }

    override fun getSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            weekStartDay = prefs[Keys.WEEK_START_DAY]?.toDayOfWeekOrNull() ?: DayOfWeek.MONDAY,
            weeklyHolidays = (prefs[Keys.WEEKLY_HOLIDAYS] ?: DEFAULT_HOLIDAYS)
                .mapNotNull { it.toIntOrNull()?.toDayOfWeekOrNull() }
                .toSet(),
            themeMode = prefs[Keys.THEME_MODE].toEnumOr(ThemeMode.SYSTEM),
            appFont = prefs[Keys.APP_FONT].toEnumOr(AppFont.Default),
            colorPreferences = ColorPreferences(
                useWallpaperColors = prefs[Keys.WALLPAPER_COLORS] ?: false,
                seedColor = prefs[Keys.SEED_COLOR] ?: ColorPreferences.DEFAULT_SEED_COLOR,
                style = prefs[Keys.COLOR_STYLE].toEnumOr(ColorStyle.EXPRESSIVE),
                pureBlack = prefs[Keys.PURE_BLACK] ?: false,
            ),
            hasCompletedOnboarding = prefs[Keys.ONBOARDED] ?: false,
        )
    }

    override suspend fun setWeekStartDay(day: DayOfWeek) {
        dataStore.edit { it[Keys.WEEK_START_DAY] = day.value }
    }

    override suspend fun toggleWeeklyHoliday(day: DayOfWeek) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.WEEKLY_HOLIDAYS] ?: DEFAULT_HOLIDAYS
            val value = day.value.toString()
            prefs[Keys.WEEKLY_HOLIDAYS] = if (value in current) current - value else current + value
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun setAppFont(font: AppFont) {
        dataStore.edit { it[Keys.APP_FONT] = font.name }
    }

    override suspend fun setUseWallpaperColors(enabled: Boolean) {
        dataStore.edit { it[Keys.WALLPAPER_COLORS] = enabled }
    }

    override suspend fun setSeedColor(argb: Int) {
        dataStore.edit { it[Keys.SEED_COLOR] = argb }
    }

    override suspend fun setColorStyle(style: ColorStyle) {
        dataStore.edit { it[Keys.COLOR_STYLE] = style.name }
    }

    override suspend fun setPureBlack(enabled: Boolean) {
        dataStore.edit { it[Keys.PURE_BLACK] = enabled }
    }

    override suspend fun setOnboardingComplete() {
        dataStore.edit { it[Keys.ONBOARDED] = true }
    }
}

private val DEFAULT_HOLIDAYS = setOf(DayOfWeek.SUNDAY.value.toString())

private fun Int.toDayOfWeekOrNull(): DayOfWeek? = if (this in 1..7) DayOfWeek.of(this) else null

private inline fun <reified T : Enum<T>> String?.toEnumOr(fallback: T): T =
    this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: fallback
