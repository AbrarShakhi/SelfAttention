package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository,
) {
    operator fun invoke(): Flow<List<Course>> = repository.getCourses()
}
