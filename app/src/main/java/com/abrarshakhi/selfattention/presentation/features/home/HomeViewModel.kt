package com.abrarshakhi.selfattention.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.OverallStats
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val getCourseStats: GetCourseStatsUseCase,
    private val getNextClass: GetNextClassUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        observeData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            getCourses().flatMapLatest { courses ->
                if (courses.isEmpty()) {
                    flowOf(
                        Triple(courses, emptyMap<Long, CourseStats>(), OverallStats(0, 0, 0f))
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
                        Triple(courses, statsMap, overall)
                    }
                }
            }.collect { (courses, statsMap, overall) ->
                _state.update {
                    it.copy(
                        courses = courses,
                        statsMap = statsMap,
                        overallStats = overall,
                        nextClass = getNextClass(courses),
                        isLoading = false,
                    )
                }
            }
        }
    }
}
