package com.abrarshakhi.selfattention.domain.backup

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.Course

/** A whole-app snapshot: every course with its schedule, plus the attendance recorded against it. */
data class BackupData(
    val courses: List<Course>,
    val attendance: List<AttendanceRecord>,
)

/**
 * Thrown when a file is not a usable backup. The [message] is shown to the user verbatim, so it
 * must explain what is wrong in plain language rather than quote a parser error.
 */
class BackupFormatException(message: String) : Exception(message)

/** Reads and writes the backup file format. Implemented in the data layer. */
interface BackupCodec {
    fun encode(data: BackupData): String

    /** @throws BackupFormatException if the text is not a valid backup. */
    fun decode(text: String): BackupData
}

sealed interface ImportResult {
    data class Success(val courses: Int, val attendance: Int) : ImportResult
    data class Failure(val message: String) : ImportResult
}
