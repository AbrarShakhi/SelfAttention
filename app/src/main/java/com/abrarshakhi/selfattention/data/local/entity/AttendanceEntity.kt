package com.abrarshakhi.selfattention.data.local.entity

import androidx.room.Entity
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import java.time.LocalDate

@Entity(tableName = "attendance", primaryKeys = ["subjectId", "epochDay"])
data class AttendanceEntity(
    val subjectId: Long,
    val epochDay: Long,
    val status: String,
)

fun AttendanceEntity.toDomain() = AttendanceRecord(
    subjectId = subjectId,
    date = LocalDate.ofEpochDay(epochDay),
    status = AttendanceStatus.valueOf(status),
)

fun AttendanceRecord.toEntity() = AttendanceEntity(
    subjectId = subjectId,
    epochDay = date.toEpochDay(),
    status = status.name,
)
