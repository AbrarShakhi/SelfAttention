package com.abrarshakhi.selfattention.presentation.features.coursedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetAttendanceForSubjectUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetSubjectStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.domain.usecase.subject.GetSubjectByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val getSubjectById: GetSubjectByIdUseCase,
    private val getAttendance: GetAttendanceForSubjectUseCase,
    private val getSubjectStats: GetSubjectStatsUseCase,
    private val markAttendance: MarkAttendanceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseDetailUiState())
    val state: StateFlow<CourseDetailUiState> = _state

    fun load(subjectId: Long) {
        viewModelScope.launch {
            val subject = getSubjectById(subjectId) ?: return@launch
            _state.update { it.copy(subject = subject) }
            combine(
                getAttendance(subjectId),
                getSubjectStats(subject),
            ) { records, stats ->
                Pair(records.associateBy { it.date }, stats)
            }.collect { (recordMap, stats) ->
                _state.update { it.copy(records = recordMap, stats = stats, isLoading = false) }
            }
        }
    }

    fun previousMonth() = _state.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
    fun nextMonth() = _state.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }

    fun openSheet(date: LocalDate) {
        val subject = _state.value.subject ?: return
        if (subject.scheduleDays.contains(date.dayOfWeek)) {
            _state.update { it.copy(sheetDate = date) }
        }
    }

    fun closeSheet() = _state.update { it.copy(sheetDate = null) }

    fun mark(status: AttendanceStatus) {
        val date = _state.value.sheetDate ?: return
        val subjectId = _state.value.subject?.id ?: return
        viewModelScope.launch {
            markAttendance(subjectId, date, status)
            closeSheet()
        }
    }

    fun clear() {
        val date = _state.value.sheetDate ?: return
        val subjectId = _state.value.subject?.id ?: return
        viewModelScope.launch {
            markAttendance.clear(subjectId, date)
            closeSheet()
        }
    }
}
