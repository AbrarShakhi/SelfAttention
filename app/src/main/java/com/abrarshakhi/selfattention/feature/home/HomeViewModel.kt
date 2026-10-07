package com.abrarshakhi.selfattention.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.common.time.TimeTicker
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetScheduleForDateUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.OverallStats
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getCourses: GetCoursesUseCase,
    private val getCourseStats: GetCourseStatsUseCase,
    private val getNextClass: GetNextClassUseCase,
    private val getSchedule: GetScheduleForDateUseCase,
    private val markAttendance: MarkAttendanceUseCase,
    settingsRepository: SettingsRepository,
    timeTicker: TimeTicker,
) : ViewModel() {

    private val summaries: Flow<List<CourseSummary>> = getCourses().flatMapLatest { courses ->
        if (courses.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(courses.map { course -> getCourseStats(course).map { CourseSummary(course, it) } }) {
                it.toList()
            }
        }
    }

    private val todaySchedule: Flow<List<ScheduledClass>> = timeTicker.minutes
        .map { it.toLocalDate() }
        .distinctUntilChanged()
        .flatMapLatest { getSchedule(it) }

    val state: StateFlow<HomeUiState> = combine(
        summaries,
        todaySchedule,
        settingsRepository.getSettings(),
        timeTicker.minutes,
    ) { courses, today, settings, now ->
        HomeUiState(
            isLoading = false,
            now = now,
            overall = OverallStats.of(courses.mapNotNull { it.stats }),
            nextClass = getNextClass(courses.map { it.course }, settings.weeklyHolidays),
            today = today,
            courses = courses,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun mark(scheduled: ScheduledClass, status: AttendanceStatus?) {
        viewModelScope.launch {
            if (status == null) {
                markAttendance.clear(scheduled.course.id, scheduled.date)
            } else {
                markAttendance(scheduled.course.id, scheduled.date, status)
            }
        }
    }
}
