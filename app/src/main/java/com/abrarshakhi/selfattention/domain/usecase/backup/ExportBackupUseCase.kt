package com.abrarshakhi.selfattention.domain.usecase.backup

import com.abrarshakhi.selfattention.domain.backup.BackupCodec
import com.abrarshakhi.selfattention.domain.backup.BackupData
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Serialises every course and attendance record to the backup format. */
class ExportBackupUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val codec: BackupCodec,
) {
    suspend operator fun invoke(): String = codec.encode(
        BackupData(
            courses = courseRepository.getCourses().first(),
            attendance = attendanceRepository.getAllAttendance().first(),
        ),
    )
}
