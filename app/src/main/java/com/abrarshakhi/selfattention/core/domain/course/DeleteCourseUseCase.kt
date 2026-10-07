package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
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
