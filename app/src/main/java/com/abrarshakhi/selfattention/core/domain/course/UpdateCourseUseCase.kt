package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
import javax.inject.Inject

class UpdateCourseUseCase @Inject constructor(
    private val repository: CourseRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(course: Course) {
        repository.getCourseById(course.id)?.let { alarmScheduler.cancelForCourse(it) }
        repository.updateCourse(course)
        alarmScheduler.scheduleForCourse(course)
    }
}
