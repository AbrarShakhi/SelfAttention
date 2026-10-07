package com.abrarshakhi.selfattention.core.data.repository

import com.abrarshakhi.selfattention.core.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<List<Course>>
    suspend fun getCourseById(id: Long): Course?
    suspend fun insertCourse(course: Course): Long
    suspend fun updateCourse(course: Course)
    suspend fun deleteCourse(course: Course)
}
