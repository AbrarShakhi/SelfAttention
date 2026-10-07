package com.abrarshakhi.selfattention.core.data.repository

import com.abrarshakhi.selfattention.core.database.dao.AttendanceDao
import com.abrarshakhi.selfattention.core.database.entity.AttendanceEntity
import com.abrarshakhi.selfattention.core.database.entity.toDomain
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class DefaultAttendanceRepository @Inject constructor(
    private val dao: AttendanceDao,
) : AttendanceRepository {

    override fun getAttendanceForCourse(courseId: Long): Flow<List<AttendanceRecord>> =
        dao.getForCourse(courseId).map { list -> list.map { it.toDomain() } }

    override fun getAttendanceForDate(date: LocalDate): Flow<List<AttendanceRecord>> =
        dao.getForDate(date.toEpochDay()).map { list -> list.map { it.toDomain() } }

    override fun getAllAttendance(): Flow<List<AttendanceRecord>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecordForCourseAndDate(courseId: Long, date: LocalDate): AttendanceRecord? =
        dao.getRecord(courseId, date.toEpochDay())?.toDomain()

    override suspend fun upsertRecord(courseId: Long, date: LocalDate, status: AttendanceStatus) {
        dao.upsert(AttendanceEntity(courseId, date.toEpochDay(), status.name))
    }

    override suspend fun deleteRecord(courseId: Long, date: LocalDate) {
        dao.delete(courseId, date.toEpochDay())
    }

    override suspend fun deleteAllForCourse(courseId: Long) {
        dao.deleteAllForCourse(courseId)
    }
}
