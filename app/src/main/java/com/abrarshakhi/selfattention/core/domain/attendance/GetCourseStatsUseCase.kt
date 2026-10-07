package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.model.meetsOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class GetCourseStatsUseCase @Inject constructor(
    private val repository: AttendanceRepository,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(course: Course): Flow<CourseStats> =
        combine(
            repository.getAttendanceForCourse(course.id),
            settingsRepository.getSettings(),
        ) { records, settings ->
            computeStats(course, records, settings.weeklyHolidays)
        }

    private fun computeStats(
        course: Course,
        records: List<AttendanceRecord>,
        weeklyHolidays: Set<DayOfWeek>,
    ): CourseStats {
        val today = LocalDate.now()
        val scheduledDates = generateSequence(course.createdOn()) { it.plusDays(1) }
            .takeWhile { !it.isAfter(today) }
            .filter { course.meetsOn(it, weeklyHolidays) }
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
}
