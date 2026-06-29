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

    override fun getAttendanceForSubject(subjectId: Long): Flow<List<AttendanceRecord>> =
        dao.getForSubject(subjectId).map { list -> list.map { it.toDomain() } }

    override fun getAttendanceForDate(date: LocalDate): Flow<List<AttendanceRecord>> =
        dao.getForDate(date.toEpochDay()).map { list -> list.map { it.toDomain() } }

    override fun getAllAttendance(): Flow<List<AttendanceRecord>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecordForSubjectAndDate(subjectId: Long, date: LocalDate): AttendanceRecord? =
        dao.getRecord(subjectId, date.toEpochDay())?.toDomain()

    override suspend fun upsertRecord(subjectId: Long, date: LocalDate, status: AttendanceStatus) {
        dao.upsert(AttendanceEntity(subjectId, date.toEpochDay(), status.name))
    }

    override suspend fun deleteRecord(subjectId: Long, date: LocalDate) {
        dao.delete(subjectId, date.toEpochDay())
    }

    override suspend fun deleteAllForSubject(subjectId: Long) {
        dao.deleteAllForSubject(subjectId)
    }
}
