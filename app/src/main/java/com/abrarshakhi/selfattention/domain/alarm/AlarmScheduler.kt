package com.abrarshakhi.selfattention.domain.alarm

import com.abrarshakhi.selfattention.domain.model.Course

interface AlarmScheduler {
    fun scheduleForCourse(course: Course)
    fun cancelForCourse(course: Course)
    fun rescheduleAll(courses: List<Course>)
}
