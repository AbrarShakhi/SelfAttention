package com.abrarshakhi.selfattention.core.data.repository

import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {
    fun getAttendanceForCourse(courseId: Long): Flow<List<AttendanceRecord>>
    fun getAttendanceForDate(date: LocalDate): Flow<List<AttendanceRecord>>
    fun getAllAttendance(): Flow<List<AttendanceRecord>>
    suspend fun getRecordForCourseAndDate(courseId: Long, date: LocalDate): AttendanceRecord?
    suspend fun upsertRecord(courseId: Long, date: LocalDate, status: AttendanceStatus)
    suspend fun deleteRecord(courseId: Long, date: LocalDate)
    suspend fun deleteAllForCourse(courseId: Long)
}
