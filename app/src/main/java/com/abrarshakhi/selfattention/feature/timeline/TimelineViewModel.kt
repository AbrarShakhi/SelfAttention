package com.abrarshakhi.selfattention.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.core.common.time.TimeTicker
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.attendance.GetScheduleForDateUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    getCourses: GetCoursesUseCase,
    getSchedule: GetScheduleForDateUseCase,
    settingsRepository: SettingsRepository,
    timeTicker: TimeTicker,
    private val markAttendance: MarkAttendanceUseCase,
) : ViewModel() {

    private val selectedDay = MutableStateFlow(LocalDate.now())
    private val visibleMonth = MutableStateFlow(YearMonth.now())

    val state: StateFlow<TimelineUiState> = combine(
        combine(selectedDay, visibleMonth, ::Pair),
        selectedDay.flatMapLatest { day -> getSchedule(day).map { DaySchedule(day, it) } },
        getCourses(),
        settingsRepository.getSettings(),
        timeTicker.minutes,
    ) { (day, month), schedule, courses, settings, now ->
        TimelineUiState(
            selectedDay = day,
            visibleMonth = month,
            schedule = schedule,
            classCountByWeekday = DayOfWeek.entries
                .associateWith { weekday -> courses.count { weekday in it.scheduleDays } }
                .filterValues { it > 0 },
            weekStartDay = settings.weekStartDay,
            weeklyHolidays = settings.weeklyHolidays,
            now = now,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineUiState())

    fun selectDay(date: LocalDate) {
        selectedDay.value = date
        visibleMonth.value = YearMonth.from(date)
    }

    fun showPreviousMonth() {
        visibleMonth.value = visibleMonth.value.minusMonths(1)
    }

    fun showNextMonth() {
        visibleMonth.value = visibleMonth.value.plusMonths(1)
    }

    fun showToday() = selectDay(LocalDate.now())

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
