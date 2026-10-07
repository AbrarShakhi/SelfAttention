package com.abrarshakhi.selfattention.feature.course.form

import com.abrarshakhi.selfattention.core.model.Course
import java.time.DayOfWeek
import java.time.LocalTime

data class CourseFormState(
    val name: String = "",
    val code: String = "",
    val days: Set<DayOfWeek> = emptySet(),
    val classTime: LocalTime = LocalTime.of(9, 0),
    val durationMinutes: Int = 60,
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 15,
) {
    val canSave: Boolean get() = name.isNotBlank() && days.isNotEmpty()

    fun reduce(event: CourseFormEvent): CourseFormState = when (event) {
        is CourseFormEvent.NameChanged -> copy(name = event.value)
        is CourseFormEvent.CodeChanged -> copy(code = event.value)
        is CourseFormEvent.DayToggled -> copy(days = if (event.day in days) days - event.day else days + event.day)
        is CourseFormEvent.TimeChanged -> copy(classTime = event.time)
        is CourseFormEvent.DurationChanged -> copy(durationMinutes = event.minutes)
        is CourseFormEvent.ReminderToggled -> copy(hasReminder = event.enabled)
        is CourseFormEvent.ReminderLeadChanged -> copy(reminderMinutesBefore = event.minutes)
    }

    fun applyTo(course: Course): Course = course.copy(
        name = name.trim(),
        code = code.trim(),
        scheduleDays = days.sortedBy { it.value },
        classHour = classTime.hour,
        classMinute = classTime.minute,
        classDurationMinutes = durationMinutes,
        hasReminder = hasReminder,
        reminderMinutesBefore = reminderMinutesBefore,
    )

    fun toNewCourse(): Course = Course(
        name = name.trim(),
        code = code.trim(),
        scheduleDays = days.sortedBy { it.value },
        classHour = classTime.hour,
        classMinute = classTime.minute,
        classDurationMinutes = durationMinutes,
        hasReminder = hasReminder,
        reminderMinutesBefore = reminderMinutesBefore,
    )

    companion object {
        val DurationOptions = listOf(45, 60, 90, 120)
        val ReminderOptions = listOf(5, 10, 15, 30, 60)

        fun from(course: Course): CourseFormState = CourseFormState(
            name = course.name,
            code = course.code,
            days = course.scheduleDays.toSet(),
            classTime = course.classTime,
            durationMinutes = course.classDurationMinutes,
            hasReminder = course.hasReminder,
            reminderMinutesBefore = course.reminderMinutesBefore,
        )
    }
}

sealed interface CourseFormEvent {
    data class NameChanged(val value: String) : CourseFormEvent
    data class CodeChanged(val value: String) : CourseFormEvent
    data class DayToggled(val day: DayOfWeek) : CourseFormEvent
    data class TimeChanged(val time: LocalTime) : CourseFormEvent
    data class DurationChanged(val minutes: Int) : CourseFormEvent
    data class ReminderToggled(val enabled: Boolean) : CourseFormEvent
    data class ReminderLeadChanged(val minutes: Int) : CourseFormEvent
}
