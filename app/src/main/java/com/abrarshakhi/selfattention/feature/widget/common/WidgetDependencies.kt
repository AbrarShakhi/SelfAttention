package com.abrarshakhi.selfattention.feature.widget.common

import android.content.Context
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetScheduleForDateUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCoursesUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetDependencies {
    fun getCourses(): GetCoursesUseCase
    fun getCourseStats(): GetCourseStatsUseCase
    fun getNextClass(): GetNextClassUseCase
    fun getSchedule(): GetScheduleForDateUseCase
    fun markAttendance(): MarkAttendanceUseCase
    fun settingsRepository(): SettingsRepository
    fun widgetUpdater(): WidgetUpdater
}

fun Context.widgetDependencies(): WidgetDependencies =
    EntryPointAccessors.fromApplication(applicationContext, WidgetDependencies::class.java)
