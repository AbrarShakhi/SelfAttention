package com.abrarshakhi.selfattention.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abrarshakhi.selfattention.core.database.dao.AttendanceDao
import com.abrarshakhi.selfattention.core.database.dao.CourseDao
import com.abrarshakhi.selfattention.core.database.entity.AttendanceEntity
import com.abrarshakhi.selfattention.core.database.entity.CourseEntity

@Database(
    entities = [CourseEntity::class, AttendanceEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun attendanceDao(): AttendanceDao
}
