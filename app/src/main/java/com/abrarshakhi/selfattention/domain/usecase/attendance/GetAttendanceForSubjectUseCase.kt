package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceForSubjectUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(subjectId: Long): Flow<List<AttendanceRecord>> =
        repository.getAttendanceForSubject(subjectId)
}
