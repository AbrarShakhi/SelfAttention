package com.abrarshakhi.selfattention.domain.usecase.course

import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourseByIdUseCase @Inject constructor(
    private val repository: CourseRepository,
) {
    suspend operator fun invoke(id: Long): Course? = repository.getCourseById(id)
}
