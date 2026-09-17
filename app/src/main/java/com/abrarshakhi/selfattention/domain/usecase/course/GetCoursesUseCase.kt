package com.abrarshakhi.selfattention.domain.usecase.course

import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository,
) {
    operator fun invoke(): Flow<List<Course>> = repository.getCourses()
}
