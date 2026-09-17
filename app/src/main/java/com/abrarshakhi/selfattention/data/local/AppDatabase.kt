package com.abrarshakhi.selfattention.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abrarshakhi.selfattention.data.local.converter.Converters
import com.abrarshakhi.selfattention.data.local.dao.AttendanceDao
import com.abrarshakhi.selfattention.data.local.dao.CourseDao
import com.abrarshakhi.selfattention.data.local.entity.AttendanceEntity
import com.abrarshakhi.selfattention.data.local.entity.CourseEntity

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
