package com.abrarshakhi.selfattention.presentation.features.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
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
                    ) { courses, records ->
                        val recordsByCourse = records.associateBy { it.courseId }
                        val classes = courses
                            .filter { date.dayOfWeek in it.scheduleDays }
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
                        Triple(date, classes, counts)
                    }
                }
                .collect { (date, classes, counts) ->
                    _state.update {
                        it.copy(
                            selectedDay = date,
                            classesForDay = classes,
                            classCountByWeekday = counts,
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
}
