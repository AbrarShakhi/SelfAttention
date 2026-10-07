package com.abrarshakhi.selfattention.feature.widget.common

import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import java.time.DayOfWeek
import java.time.LocalDate

object WidgetPreviews {
    val courses = listOf(
        Course(id = 1, name = "Databases", code = "CS-201", scheduleDays = listOf(DayOfWeek.MONDAY), classHour = 9, classMinute = 30),
        Course(id = 2, name = "Linear Algebra", code = "MA-110", scheduleDays = listOf(DayOfWeek.MONDAY), classHour = 11, classMinute = 0),
        Course(id = 3, name = "Physics Lab", code = "PH-150", scheduleDays = listOf(DayOfWeek.MONDAY), classHour = 14, classMinute = 0),
    )

    val stats = listOf(
        CourseStats(courseId = 1, totalScheduled = 20, present = 17, absent = 2, holiday = 1),
        CourseStats(courseId = 2, totalScheduled = 18, present = 11, absent = 5, holiday = 0),
        CourseStats(courseId = 3, totalScheduled = 10, present = 9, absent = 1, holiday = 0),
    )

    fun today(date: LocalDate = LocalDate.now()): List<ScheduledClass> = courses.mapIndexed { index, course ->
        val status = listOf(AttendanceStatus.PRESENT, null, null)[index]
        ScheduledClass(
            course = course,
            date = date,
            record = status?.let { AttendanceRecord(courseId = course.id, date = date, status = it) },
        )
    }
}
