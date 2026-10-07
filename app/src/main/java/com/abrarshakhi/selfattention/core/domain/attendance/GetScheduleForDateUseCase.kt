package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import com.abrarshakhi.selfattention.core.model.meetsOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetScheduleForDateUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<ScheduledClass>> = combine(
        courseRepository.getCourses(),
        attendanceRepository.getAttendanceForDate(date),
        settingsRepository.getSettings(),
    ) { courses, records, settings ->
        val recordByCourse = records.associateBy { it.courseId }
        courses
            .filter { it.meetsOn(date, settings.weeklyHolidays) }
            .sortedBy { it.classTime }
            .map { ScheduledClass(course = it, date = date, record = recordByCourse[it.id]) }
    }
}
