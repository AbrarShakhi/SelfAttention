package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceForCourseUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(courseId: Long): Flow<List<AttendanceRecord>> =
        repository.getAttendanceForCourse(courseId)
}
