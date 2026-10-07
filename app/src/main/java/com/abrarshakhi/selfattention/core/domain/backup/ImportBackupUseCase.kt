package com.abrarshakhi.selfattention.core.domain.backup

import com.abrarshakhi.selfattention.core.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.core.data.backup.BackupCodec
import com.abrarshakhi.selfattention.core.data.backup.BackupData
import com.abrarshakhi.selfattention.core.data.backup.BackupFileStore
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val alarmScheduler: AlarmScheduler,
    private val codec: BackupCodec,
    private val fileStore: BackupFileStore,
) {
    suspend operator fun invoke(sourceUri: String): ImportResult {
        val data = try {
            codec.decode(fileStore.read(sourceUri))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImportResult.Failure(e.message ?: UNREADABLE)
        }

        return try {
            restore(data)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ImportResult.Failure("Could not save the imported courses: ${e.message ?: "unknown error"}")
        }
    }

    private suspend fun restore(data: BackupData): ImportResult.Success {
        val newIds = data.courses.associate { course ->
            val newId = courseRepository.insertCourse(course.copy(id = 0))
            alarmScheduler.scheduleForCourse(course.copy(id = newId))
            course.id to newId
        }

        val restored = data.attendance.count { record ->
            val courseId = newIds[record.courseId] ?: return@count false
            attendanceRepository.upsertRecord(courseId, record.date, record.status)
            true
        }

        return ImportResult.Success(courses = data.courses.size, attendance = restored)
    }

    private companion object {
        const val UNREADABLE = "That file could not be read."
    }
}
