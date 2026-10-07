package com.abrarshakhi.selfattention.core.database.entity

import androidx.room.Entity
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import java.time.LocalDate

@Entity(tableName = "attendance", primaryKeys = ["courseId", "epochDay"])
data class AttendanceEntity(
    val courseId: Long,
    val epochDay: Long,
    val status: String,
)

fun AttendanceEntity.toDomain() = AttendanceRecord(
    courseId = courseId,
    date = LocalDate.ofEpochDay(epochDay),
    status = AttendanceStatus.valueOf(status),
)

fun AttendanceRecord.toEntity() = AttendanceEntity(
    courseId = courseId,
    epochDay = date.toEpochDay(),
    status = status.name,
)
