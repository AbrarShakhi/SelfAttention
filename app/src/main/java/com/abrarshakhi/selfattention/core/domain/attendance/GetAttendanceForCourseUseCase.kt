package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceForCourseUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(courseId: Long): Flow<List<AttendanceRecord>> =
        repository.getAttendanceForCourse(courseId)
}
