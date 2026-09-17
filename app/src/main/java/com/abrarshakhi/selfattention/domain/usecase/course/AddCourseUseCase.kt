package com.abrarshakhi.selfattention.domain.usecase.course

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
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
