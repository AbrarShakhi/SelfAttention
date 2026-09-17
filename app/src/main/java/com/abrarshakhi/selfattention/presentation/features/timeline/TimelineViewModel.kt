package com.abrarshakhi.selfattention.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.meetsOn
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import com.abrarshakhi.selfattention.domain.usecase.course.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val attendanceRepository: AttendanceRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TimelineUiState())
    val state: StateFlow<TimelineUiState> = _state

    private val selectedDay = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            selectedDay
                .flatMapLatest { date ->
                    combine(
                        getCourses(),
                        attendanceRepository.getAttendanceForDate(date),
                        settingsRepository.getSettings(),
                    ) { courses, records, settings ->
                        val recordsByCourse = records.associateBy { it.courseId }
                        val classes = courses
                            .filter { it.meetsOn(date, settings.weeklyHolidays) }
                            .sortedBy { it.classHour * 60 + it.classMinute }
                            .map { course ->
                                ScheduledClass(
                                    course = course,
                                    date = date,
                                    record = recordsByCourse[course.id],
                                )
                            }
                        val counts = DayOfWeek.entries
                            .associateWith { dow -> courses.count { dow in it.scheduleDays } }
                            .filterValues { it > 0 }
                        DayLoad(date, classes, counts, settings)
                    }
                }
                .collect { load ->
                    _state.update {
                        it.copy(
                            selectedDay = load.date,
                            classesForDay = load.classes,
                            classCountByWeekday = load.counts,
                            weekStartDay = load.settings.weekStartDay,
                            weeklyHolidays = load.settings.weeklyHolidays,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun selectDay(date: LocalDate) {
        _state.update { it.copy(selectedDay = date, visibleMonth = YearMonth.from(date)) }
        selectedDay.value = date
    }

    fun showPreviousMonth() =
        _state.update { it.copy(visibleMonth = it.visibleMonth.minusMonths(1)) }

    fun showNextMonth() =
        _state.update { it.copy(visibleMonth = it.visibleMonth.plusMonths(1)) }

    fun showToday() = selectDay(LocalDate.now())

    private data class DayLoad(
        val date: LocalDate,
        val classes: List<ScheduledClass>,
        val counts: Map<DayOfWeek, Int>,
        val settings: AppSettings,
    )
}
