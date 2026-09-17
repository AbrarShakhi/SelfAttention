package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetCourseStatsUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(course: Course): Flow<CourseStats> =
        repository.getAttendanceForCourse(course.id).map { records ->
            computeStats(course, records)
        }

    private fun computeStats(course: Course, records: List<AttendanceRecord>): CourseStats {
        val today = LocalDate.now()
        val scheduledDates = generateSequence(course.createdAt.toLocalDate()) { it.plusDays(1) }
            .takeWhile { !it.isAfter(today) }
            .filter { course.scheduleDays.contains(it.dayOfWeek) }
            .toList()

        val present = records.count { it.status == AttendanceStatus.PRESENT }
        val absent = records.count { it.status == AttendanceStatus.ABSENT }
        val holiday = records.count { it.status == AttendanceStatus.HOLIDAY }

        return CourseStats(
            courseId = course.id,
            totalScheduled = scheduledDates.size,
            present = present,
            absent = absent,
            holiday = holiday,
        )
    }

    private fun Long.toLocalDate(): LocalDate =
        LocalDate.ofEpochDay(this / (24L * 60 * 60 * 1000))
}
