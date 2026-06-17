package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.Subject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class GetNextClassUseCase @Inject constructor() {
    operator fun invoke(subjects: List<Subject>): NextClass? {
        val now = LocalDateTime.now()
        return subjects
            .flatMap { subject -> upcomingOccurrences(subject, now) }
            .minByOrNull { it.scheduledAt }
    }

    private fun upcomingOccurrences(subject: Subject, from: LocalDateTime): List<NextClass> {
        val result = mutableListOf<NextClass>()
        var checkDate = from.toLocalDate()
        repeat(8) { offset ->
            if (offset > 0) checkDate = checkDate.plusDays(1)
            if (subject.scheduleDays.contains(checkDate.dayOfWeek)) {
                val classTime = LocalTime.of(subject.classHour, subject.classMinute)
                val classDateTime = LocalDateTime.of(checkDate, classTime)
                if (classDateTime.isAfter(from)) {
                    result.add(NextClass(subject = subject, scheduledAt = classDateTime))
                }
            }
        }
        return result
    }
}
