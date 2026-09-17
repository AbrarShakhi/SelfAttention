package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.meetsOn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetNextClassUseCase @Inject constructor() {
    operator fun invoke(
        courses: List<Course>,
        weeklyHolidays: Set<DayOfWeek> = emptySet(),
    ): NextClass? {
        val now = LocalDateTime.now()
        return courses
            .flatMap { course -> upcomingOccurrences(course, now, weeklyHolidays) }
            .minByOrNull { it.scheduledAt }
    }

    private fun upcomingOccurrences(
        course: Course,
        from: LocalDateTime,
        weeklyHolidays: Set<DayOfWeek>,
    ): List<NextClass> {
        val result = mutableListOf<NextClass>()
        var checkDate = from.toLocalDate()
        repeat(8) { offset ->
            if (offset > 0) checkDate = checkDate.plusDays(1)
            if (course.meetsOn(checkDate, weeklyHolidays)) {
                val classTime = LocalTime.of(course.classHour, course.classMinute)
                val classDateTime = LocalDateTime.of(checkDate, classTime)
                if (classDateTime.isAfter(from)) {
                    result.add(NextClass(course = course, scheduledAt = classDateTime))
                }
            }
        }
        return result
    }
}
