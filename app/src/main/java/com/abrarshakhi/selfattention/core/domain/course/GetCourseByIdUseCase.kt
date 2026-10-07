package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
import javax.inject.Inject

class GetCourseByIdUseCase @Inject constructor(
    private val repository: CourseRepository,
) {
    suspend operator fun invoke(id: Long): Course? = repository.getCourseById(id)
}
