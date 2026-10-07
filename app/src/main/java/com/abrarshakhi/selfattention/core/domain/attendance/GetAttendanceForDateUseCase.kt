package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetAttendanceForDateUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<AttendanceRecord>> =
        repository.getAttendanceForDate(date)
}
