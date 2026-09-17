package com.abrarshakhi.selfattention.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abrarshakhi.selfattention.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE courseId = :courseId")
    fun getForCourse(courseId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE epochDay = :epochDay")
    fun getForDate(epochDay: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance")
    fun getAll(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE courseId = :courseId AND epochDay = :epochDay LIMIT 1")
    suspend fun getRecord(courseId: Long, epochDay: Long): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: AttendanceEntity)

    @Query("DELETE FROM attendance WHERE courseId = :courseId AND epochDay = :epochDay")
    suspend fun delete(courseId: Long, epochDay: Long)

    @Query("DELETE FROM attendance WHERE courseId = :courseId")
    suspend fun deleteAllForCourse(courseId: Long)
}
