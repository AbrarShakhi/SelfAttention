package com.abrarshakhi.selfattention.domain.repository

import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun setWeekStartDay(day: DayOfWeek)
    suspend fun setWeeklyHolidays(days: Set<DayOfWeek>)
    suspend fun setThemeMode(mode: ThemeMode)
}
