package com.abrarshakhi.selfattention.domain.usecase.course

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import javax.inject.Inject

/**
 * Updates a course and re-arms its alarms.
 *
 * The cancel step uses the *stored* course, not the incoming one: alarm request codes are derived
 * from the schedule days, so cancelling with the new days would leave alarms armed for any day the
 * user removed. `scheduleForCourse` does not cancel first, which is why this has to.
 *
 * Attendance rows are keyed by `(courseId, epochDay)` and are deliberately left untouched — days
 * dropped from the schedule keep their history rather than silently losing it.
 */
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
