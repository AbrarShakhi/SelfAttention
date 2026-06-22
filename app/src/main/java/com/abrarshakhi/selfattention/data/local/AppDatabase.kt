package com.abrarshakhi.selfattention.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abrarshakhi.selfattention.data.local.converter.Converters
import com.abrarshakhi.selfattention.data.local.dao.AttendanceDao
import com.abrarshakhi.selfattention.data.local.dao.SubjectDao
import com.abrarshakhi.selfattention.data.local.entity.AttendanceEntity
import com.abrarshakhi.selfattention.data.local.entity.SubjectEntity

@Database(
    entities = [SubjectEntity::class, AttendanceEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceDao(): AttendanceDao
}
