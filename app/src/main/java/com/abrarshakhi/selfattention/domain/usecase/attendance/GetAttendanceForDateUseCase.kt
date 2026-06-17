package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceForDateUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<AttendanceRecord>> =
        repository.getAttendanceForDate(date)
}
