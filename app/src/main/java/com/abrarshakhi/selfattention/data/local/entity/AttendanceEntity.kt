package com.abrarshakhi.selfattention.data.local.entity

import androidx.room.Entity
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
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
