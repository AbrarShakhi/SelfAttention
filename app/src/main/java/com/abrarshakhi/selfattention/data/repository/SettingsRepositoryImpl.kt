package com.abrarshakhi.selfattention.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
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
            weeklyHolidays = (prefs[Keys.WEEKLY_HOLIDAYS] ?: setOf("7"))
                .map { DayOfWeek.of(it.toInt()) }.toSet(),
            themeMode = prefs[Keys.THEME_MODE].toEnumOr(ThemeMode.SYSTEM),
            appFont = prefs[Keys.APP_FONT].toEnumOr(AppFont.Default),
            hasCompletedOnboarding = prefs[Keys.ONBOARDED] ?: false,
        )
    }

    override suspend fun setWeekStartDay(day: DayOfWeek) {
        dataStore.edit { it[Keys.WEEK_START_DAY] = day.value }
    }

    override suspend fun setWeeklyHolidays(days: Set<DayOfWeek>) {
        dataStore.edit { it[Keys.WEEKLY_HOLIDAYS] = days.map { d -> d.value.toString() }.toSet() }
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

/**
 * Reads a persisted enum name, falling back instead of throwing.
 *
 * `enumValueOf` throws on a constant that has been renamed or removed, and this runs while the
 * app is reading settings at launch — so a stale value would crash before any UI appears.
 */
private inline fun <reified T : Enum<T>> String?.toEnumOr(fallback: T): T =
    this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: fallback
