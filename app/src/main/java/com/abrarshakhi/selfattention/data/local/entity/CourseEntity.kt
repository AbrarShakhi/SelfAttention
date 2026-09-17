package com.abrarshakhi.selfattention.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abrarshakhi.selfattention.domain.model.Course
import java.time.DayOfWeek

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val scheduleDays: List<DayOfWeek>,
    val classHour: Int,
    val classMinute: Int,
    val classDurationMinutes: Int,
    val hasReminder: Boolean,
    val reminderMinutesBefore: Int,
    val createdAt: Long,
)

fun CourseEntity.toDomain() = Course(
    id = id,
    name = name,
    code = code,
    scheduleDays = scheduleDays,
    classHour = classHour,
    classMinute = classMinute,
    classDurationMinutes = classDurationMinutes,
    hasReminder = hasReminder,
    reminderMinutesBefore = reminderMinutesBefore,
    createdAt = createdAt,
)

fun Course.toEntity() = CourseEntity(
    id = id,
    name = name,
    code = code,
    scheduleDays = scheduleDays,
    classHour = classHour,
    classMinute = classMinute,
    classDurationMinutes = classDurationMinutes,
    hasReminder = hasReminder,
    reminderMinutesBefore = reminderMinutesBefore,
    createdAt = createdAt,
)
