package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import java.time.LocalDate
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    suspend operator fun invoke(courseId: Long, date: LocalDate, status: AttendanceStatus) {
        repository.upsertRecord(courseId, date, status)
    }

    suspend fun clear(courseId: Long, date: LocalDate) {
        repository.deleteRecord(courseId, date)
    }
}
