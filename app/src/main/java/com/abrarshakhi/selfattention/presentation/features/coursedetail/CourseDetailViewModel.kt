package com.abrarshakhi.selfattention.presentation.features.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.CourseStats
import com.abrarshakhi.selfattention.domain.model.meetsOn
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetAttendanceForCourseUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.domain.usecase.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val getAttendance: GetAttendanceForCourseUseCase,
    private val getCourseStats: GetCourseStatsUseCase,
    private val markAttendance: MarkAttendanceUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseDetailUiState())
    val state: StateFlow<CourseDetailUiState> = _state

    /**
     * Drives the whole screen.
     *
     * The course is read from the *stream* rather than fetched once, so edits made in the course
     * editor show up on the way back. `flatMapLatest` also cancels the previous subscription —
     * the earlier version launched a fresh, never-cancelled collector on every [load] call, and
     * re-entering this screen re-runs its `LaunchedEffect`.
     */
    private val courseId = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch {
            courseId
                .filterNotNull()
                .flatMapLatest { id ->
                    getCourses()
                        .map { courses -> courses.firstOrNull { it.id == id } }
                        .distinctUntilChanged()
                        .flatMapLatest { course ->
                            if (course == null) {
                                flowOf(null)
                            } else {
                                combine(
                                    getAttendance(id),
                                    getCourseStats(course),
                                    settingsRepository.getSettings(),
                                ) { records, stats, settings ->
                                    Loaded(
                                        course,
                                        records.associateBy { it.date },
                                        stats,
                                        settings,
                                    )
                                }
                            }
                        }
                }
                .collect { loaded ->
                    _state.update {
                        if (loaded == null) {
                            // Deleted while open; the caller navigates away.
                            it.copy(course = null, isLoading = false)
                        } else {
                            it.copy(
                                course = loaded.course,
                                records = loaded.records,
                                stats = loaded.stats,
                                weekStartDay = loaded.settings.weekStartDay,
                                weeklyHolidays = loaded.settings.weeklyHolidays,
                                isLoading = false,
                            )
                        }
                    }
                }
        }
    }

    fun load(courseId: Long) {
        this.courseId.value = courseId
    }

    fun previousMonth() = _state.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
    fun nextMonth() = _state.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }

    fun openSheet(date: LocalDate) {
        val course = _state.value.course ?: return
        if (course.meetsOn(date, _state.value.weeklyHolidays)) {
            _state.update { it.copy(sheetDate = date) }
        }
    }

    fun closeSheet() = _state.update { it.copy(sheetDate = null) }

    fun mark(status: AttendanceStatus) {
        val date = _state.value.sheetDate ?: return
        val courseId = _state.value.course?.id ?: return
        viewModelScope.launch {
            markAttendance(courseId, date, status)
            closeSheet()
        }
    }

    fun clear() {
        val date = _state.value.sheetDate ?: return
        val courseId = _state.value.course?.id ?: return
        viewModelScope.launch {
            markAttendance.clear(courseId, date)
            closeSheet()
        }
    }

    private data class Loaded(
        val course: Course,
        val records: Map<LocalDate, AttendanceRecord>,
        val stats: CourseStats,
        val settings: AppSettings,
    )
}
