package com.abrarshakhi.selfattention.domain.usecase.backup

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.backup.BackupCodec
import com.abrarshakhi.selfattention.domain.backup.BackupFormatException
import com.abrarshakhi.selfattention.domain.backup.ImportResult
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import javax.inject.Inject

/**
 * Restores a backup **additively** — imported courses are inserted alongside whatever is already
 * there, never replacing it. Importing the same file twice therefore duplicates it, which is a
 * far kinder failure than silently wiping the user's data.
 *
 * Courses are re-inserted, so they get fresh row ids; attendance is remapped onto them before
 * being written, otherwise every record would attach to the wrong course.
 */
class ImportBackupUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val alarmScheduler: AlarmScheduler,
    private val codec: BackupCodec,
) {
    suspend operator fun invoke(json: String): ImportResult {
        val data = try {
            codec.decode(json)
        } catch (e: BackupFormatException) {
            return ImportResult.Failure(e.message ?: "That file could not be read.")
        } catch (e: Exception) {
            // Nothing reaches the user as a stack trace.
            return ImportResult.Failure("That file could not be read.")
        }

        return try {
            val idMap = mutableMapOf<Long, Long>()
            data.courses.forEach { course ->
                val newId = courseRepository.insertCourse(course.copy(id = 0))
                idMap[course.id] = newId
                alarmScheduler.scheduleForCourse(course.copy(id = newId))
            }

            var restored = 0
            data.attendance.forEach { record ->
                val newCourseId = idMap[record.courseId] ?: return@forEach
                attendanceRepository.upsertRecord(newCourseId, record.date, record.status)
                restored++
            }

            ImportResult.Success(courses = data.courses.size, attendance = restored)
        } catch (e: Exception) {
            ImportResult.Failure("Could not save the imported courses: ${e.message ?: "unknown error"}")
        }
    }
}
