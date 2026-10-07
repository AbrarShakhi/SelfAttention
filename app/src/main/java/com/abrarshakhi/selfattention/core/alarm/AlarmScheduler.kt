package com.abrarshakhi.selfattention.core.alarm

import com.abrarshakhi.selfattention.core.model.Course
import java.time.DayOfWeek

interface AlarmScheduler {
    fun scheduleForCourse(course: Course)
    fun scheduleNext(course: Course, dayOfWeek: DayOfWeek, type: AlarmType)
    fun cancelForCourse(course: Course)
    fun rescheduleAll(courses: List<Course>)
}
