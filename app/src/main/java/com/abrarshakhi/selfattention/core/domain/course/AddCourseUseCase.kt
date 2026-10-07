package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
import javax.inject.Inject

class AddCourseUseCase @Inject constructor(
    private val repository: CourseRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(course: Course) {
        val id = repository.insertCourse(course)
        alarmScheduler.scheduleForCourse(course.copy(id = id))
    }
}
