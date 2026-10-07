package com.abrarshakhi.selfattention.core.alarm

import com.abrarshakhi.selfattention.core.model.Course
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

data class AlarmTrigger(
    val classDate: LocalDate,
    val triggerAt: LocalDateTime,
)

fun AlarmType.offsetMinutes(course: Course): Long = when (this) {
    AlarmType.PRE_CLASS -> -course.reminderMinutesBefore.toLong()
    AlarmType.POST_CLASS -> course.classDurationMinutes.toLong()
}

fun nextAlarmTrigger(
    course: Course,
    dayOfWeek: DayOfWeek,
    type: AlarmType,
    now: LocalDateTime,
): AlarmTrigger {
    val offset = type.offsetMinutes(course)
    var classDate = now.minusMinutes(offset).toLocalDate().with(TemporalAdjusters.nextOrSame(dayOfWeek))
    while (!classDate.atTime(course.classTime).plusMinutes(offset).isAfter(now)) {
        classDate = classDate.plusWeeks(1)
    }
    return AlarmTrigger(classDate = classDate, triggerAt = classDate.atTime(course.classTime).plusMinutes(offset))
}
