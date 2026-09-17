package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.Course
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetNextClassUseCase @Inject constructor() {
    operator fun invoke(courses: List<Course>): NextClass? {
        val now = LocalDateTime.now()
        return courses
            .flatMap { course -> upcomingOccurrences(course, now) }
            .minByOrNull { it.scheduledAt }
    }

    private fun upcomingOccurrences(course: Course, from: LocalDateTime): List<NextClass> {
        val result = mutableListOf<NextClass>()
        var checkDate = from.toLocalDate()
        repeat(8) { offset ->
            if (offset > 0) checkDate = checkDate.plusDays(1)
            if (course.scheduleDays.contains(checkDate.dayOfWeek)) {
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
