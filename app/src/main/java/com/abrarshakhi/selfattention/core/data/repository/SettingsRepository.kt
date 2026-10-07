package com.abrarshakhi.selfattention.core.data.repository

import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun setWeekStartDay(day: DayOfWeek)
    suspend fun toggleWeeklyHoliday(day: DayOfWeek)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setAppFont(font: AppFont)
    suspend fun setOnboardingComplete()
}
