package com.abrarshakhi.selfattention.core.data.backup

import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.Course

data class BackupData(
    val courses: List<Course>,
    val attendance: List<AttendanceRecord>,
)

class BackupFormatException(message: String) : Exception(message)

interface BackupCodec {
    fun encode(data: BackupData): String
    fun decode(text: String): BackupData
}
