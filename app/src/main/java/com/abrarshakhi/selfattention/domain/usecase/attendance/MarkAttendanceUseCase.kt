package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import java.time.LocalDate
import javax.inject.Inject

class MarkAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    suspend operator fun invoke(subjectId: Long, date: LocalDate, status: AttendanceStatus) {
        repository.upsertRecord(subjectId, date, status)
    }

    suspend fun clear(subjectId: Long, date: LocalDate) {
        repository.deleteRecord(subjectId, date)
    }
}
