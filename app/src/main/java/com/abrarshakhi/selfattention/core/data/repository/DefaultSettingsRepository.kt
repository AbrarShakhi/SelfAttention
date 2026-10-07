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
        val ONBOARDED = booleanPreferencesKey("has_completed_onboarding")
    }

    override fun getSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            weekStartDay = DayOfWeek.of(prefs[Keys.WEEK_START_DAY] ?: DayOfWeek.MONDAY.value),
            weeklyHolidays = (prefs[Keys.WEEKLY_HOLIDAYS] ?: DEFAULT_HOLIDAYS)
                .map { DayOfWeek.of(it.toInt()) }.toSet(),
            themeMode = prefs[Keys.THEME_MODE].toEnumOr(ThemeMode.SYSTEM),
            appFont = prefs[Keys.APP_FONT].toEnumOr(AppFont.Default),
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

    override suspend fun setOnboardingComplete() {
        dataStore.edit { it[Keys.ONBOARDED] = true }
    }
}

private val DEFAULT_HOLIDAYS = setOf(DayOfWeek.SUNDAY.value.toString())

private inline fun <reified T : Enum<T>> String?.toEnumOr(fallback: T): T =
    this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: fallback
