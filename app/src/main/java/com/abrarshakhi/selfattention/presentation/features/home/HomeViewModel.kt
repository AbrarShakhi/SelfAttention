package com.abrarshakhi.selfattention.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetSubjectStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.subject.GetSubjectsUseCase
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
    private val getSubjects: GetSubjectsUseCase,
    private val getSubjectStats: GetSubjectStatsUseCase,
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
            getSubjects().flatMapLatest { subjects ->
                if (subjects.isEmpty()) {
                    flowOf(
                        Triple(subjects, emptyMap<Long, SubjectStats>(), OverallStats(0, 0, 0f))
                    )
                } else {
                    val statsFlows = subjects.map { s -> getSubjectStats(s) }
                    combine(statsFlows) { statsArray ->
                        val statsMap = statsArray.associateBy { it.subjectId }
                        val totalPresent = statsArray.sumOf { it.present }
                        val totalAbsent = statsArray.sumOf { it.absent }
                        val countable = totalPresent + totalAbsent
                        val overall = OverallStats(
                            totalPresent = totalPresent,
                            totalAbsent = totalAbsent,
                            attendancePercentage = if (countable == 0) 0f
                            else totalPresent.toFloat() / countable,
                        )
                        Triple(subjects, statsMap, overall)
                    }
                }
            }.collect { (subjects, statsMap, overall) ->
                _state.update {
                    it.copy(
                        subjects = subjects,
                        statsMap = statsMap,
                        overallStats = overall,
                        nextClass = getNextClass(subjects),
                        isLoading = false,
                    )
                }
            }
        }
    }
}
