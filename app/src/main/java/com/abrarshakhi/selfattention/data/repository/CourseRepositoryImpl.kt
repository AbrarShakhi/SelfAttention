package com.abrarshakhi.selfattention.data.repository

import com.abrarshakhi.selfattention.data.local.dao.CourseDao
import com.abrarshakhi.selfattention.data.local.entity.toDomain
import com.abrarshakhi.selfattention.data.local.entity.toEntity
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val dao: CourseDao,
) : CourseRepository {

    override fun getCourses(): Flow<List<Course>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getCourseById(id: Long): Course? =
        dao.getById(id)?.toDomain()

    override suspend fun insertCourse(course: Course): Long =
        dao.insert(course.toEntity())

    override suspend fun updateCourse(course: Course) =
        dao.update(course.toEntity())

    override suspend fun deleteCourse(course: Course) =
        dao.delete(course.toEntity())
}
