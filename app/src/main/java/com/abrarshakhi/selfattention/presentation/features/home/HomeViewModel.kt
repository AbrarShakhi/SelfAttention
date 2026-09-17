package com.abrarshakhi.selfattention.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.course.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val getCourseStats: GetCourseStatsUseCase,
    private val getNextClass: GetNextClassUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        observeData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            combine(getCourses(), settingsRepository.getSettings()) { courses, settings ->
                courses to settings.weeklyHolidays
            }.flatMapLatest { (courses, holidays) ->
                if (courses.isEmpty()) {
                    flowOf(
                        HomeLoad(courses, emptyMap(), OverallStats(0, 0, 0f), holidays)
                    )
                } else {
                    val statsFlows = courses.map { s -> getCourseStats(s) }
                    combine(statsFlows) { statsArray ->
                        val statsMap = statsArray.associateBy { it.courseId }
                        val totalPresent = statsArray.sumOf { it.present }
                        val totalAbsent = statsArray.sumOf { it.absent }
                        val countable = totalPresent + totalAbsent
                        val overall = OverallStats(
                            totalPresent = totalPresent,
                            totalAbsent = totalAbsent,
                            attendancePercentage = if (countable == 0) 0f
                            else totalPresent.toFloat() / countable,
                        )
                        HomeLoad(courses, statsMap, overall, holidays)
                    }
                }
            }.collect { load ->
                _state.update {
                    it.copy(
                        courses = load.courses,
                        statsMap = load.stats,
                        overallStats = load.overall,
                        nextClass = getNextClass(load.courses, load.holidays),
                        isLoading = false,
                    )
                }
            }
        }
    }

    private data class HomeLoad(
        val courses: List<Course>,
        val stats: Map<Long, CourseStats>,
        val overall: OverallStats,
        val holidays: Set<DayOfWeek>,
    )
}
