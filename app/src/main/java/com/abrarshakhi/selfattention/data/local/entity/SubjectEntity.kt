package com.abrarshakhi.selfattention.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abrarshakhi.selfattention.domain.model.Subject
import java.time.DayOfWeek

@Entity(tableName = "subjects")
data class SubjectEntity(
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

fun SubjectEntity.toDomain() = Subject(
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

fun Subject.toEntity() = SubjectEntity(
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
