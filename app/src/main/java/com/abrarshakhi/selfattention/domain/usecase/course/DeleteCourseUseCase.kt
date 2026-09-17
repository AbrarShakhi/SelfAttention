package com.abrarshakhi.selfattention.domain.usecase.course

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import javax.inject.Inject

class DeleteCourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(course: Course) {
        alarmScheduler.cancelForCourse(course)
        attendanceRepository.deleteAllForCourse(course.id)
        courseRepository.deleteCourse(course)
    }
}
