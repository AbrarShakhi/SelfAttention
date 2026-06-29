package com.abrarshakhi.selfattention.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
    }

    override fun getSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            weekStartDay = DayOfWeek.of(prefs[Keys.WEEK_START_DAY] ?: DayOfWeek.MONDAY.value),
            weeklyHolidays = (prefs[Keys.WEEKLY_HOLIDAYS] ?: setOf("7"))
                .map { DayOfWeek.of(it.toInt()) }.toSet(),
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
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
}
