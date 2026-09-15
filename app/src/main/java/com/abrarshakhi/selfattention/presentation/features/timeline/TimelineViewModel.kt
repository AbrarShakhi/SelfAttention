package com.abrarshakhi.selfattention.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.usecase.subject.GetSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getSubjects: GetSubjectsUseCase,
    private val attendanceRepository: AttendanceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TimelineUiState())
    val state: StateFlow<TimelineUiState> = _state

    init {
        val today = LocalDate.now()
        val weekDays = (0..6).map { today.minusDays((3 - it).toLong()) }
        _state.update { it.copy(weekDays = weekDays, selectedDay = today) }
        observeDay(today)
    }

    fun selectDay(date: LocalDate) {
        _state.update { it.copy(selectedDay = date) }
        observeDay(date)
    }

    private fun observeDay(date: LocalDate) {
        viewModelScope.launch {
            combine(
                getSubjects(),
                attendanceRepository.getAttendanceForDate(date),
            ) { subjects, records ->
                val recordMap = records.associateBy { it.subjectId }
                subjects
                    .filter { it.scheduleDays.contains(date.dayOfWeek) }
                    .sortedBy { it.classHour * 60 + it.classMinute }
                    .map { subject ->
                        ScheduledClass(
                            subject = subject,
                            date = date,
                            record = recordMap[subject.id],
                        )
                    }
            }.collect { classes ->
                _state.update { it.copy(classesForDay = classes) }
            }
        }
    }
}
