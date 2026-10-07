package com.abrarshakhi.selfattention.core.domain.backup

import com.abrarshakhi.selfattention.core.data.backup.BackupCodec
import com.abrarshakhi.selfattention.core.data.backup.BackupData
import com.abrarshakhi.selfattention.core.data.backup.BackupFileStore
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val codec: BackupCodec,
    private val fileStore: BackupFileStore,
) {
    suspend operator fun invoke(destinationUri: String): ExportResult = try {
        val data = BackupData(
            courses = courseRepository.getCourses().first(),
            attendance = attendanceRepository.getAllAttendance().first(),
        )
        fileStore.write(destinationUri, codec.encode(data))
        ExportResult.Success
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ExportResult.Failure(e.message ?: "The file could not be written.")
    }
}
