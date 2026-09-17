package com.abrarshakhi.selfattention.data.repository

import com.abrarshakhi.selfattention.data.local.dao.AttendanceDao
import com.abrarshakhi.selfattention.data.local.entity.AttendanceEntity
import com.abrarshakhi.selfattention.data.local.entity.toDomain
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
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
