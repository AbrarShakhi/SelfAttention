package com.abrarshakhi.selfattention.domain.repository

import com.abrarshakhi.selfattention.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<List<Course>>
    suspend fun getCourseById(id: Long): Course?
    suspend fun insertCourse(course: Course): Long
    suspend fun updateCourse(course: Course)
    suspend fun deleteCourse(course: Course)
}
