package com.abrarshakhi.selfattention.domain.usecase

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import com.abrarshakhi.selfattention.domain.usecase.course.UpdateCourseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.DayOfWeek

/**
 * Behaviour of [UpdateCourseUseCase], whose job is to keep alarms in step with an edited course.
 *
 * Alarm request codes are derived from the schedule days, so cancelling has to use the *stored*
 * days — cancelling with the edited ones would strand alarms on every day the user removed.
 */
class UpdateCourseUseCaseTest {

    private val repository: CourseRepository = mockk(relaxed = true)
    private val alarmScheduler: AlarmScheduler = mockk(relaxed = true)
    private val updateCourse = UpdateCourseUseCase(repository, alarmScheduler)

    /** Dropping Friday must cancel against Mon+Fri, or Friday's alarm stays armed forever. */
    @Test
    fun `cancels alarms using the stored course, not the edited one`() = runTest {
        val stored = buildCourse(days = listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        val edited = stored.copy(scheduleDays = listOf(DayOfWeek.MONDAY))
        coEvery { repository.getCourseById(stored.id) } returns stored

        updateCourse(edited)

        verify(exactly = 1) { alarmScheduler.cancelForCourse(stored) }
        verify(exactly = 0) { alarmScheduler.cancelForCourse(edited) }
    }

    /** Cancel must happen before rescheduling; scheduleForCourse does not cancel on its own. */
    @Test
    fun `cancels, persists, then reschedules in that order`() = runTest {
        val stored = buildCourse(days = listOf(DayOfWeek.MONDAY))
        val edited = stored.copy(name = "Renamed", classHour = 14)
        coEvery { repository.getCourseById(stored.id) } returns stored

        updateCourse(edited)

        coVerifyOrder {
            alarmScheduler.cancelForCourse(stored)
            repository.updateCourse(edited)
            alarmScheduler.scheduleForCourse(edited)
        }
    }

    /** Turning reminders off must still re-arm: the post-class prompt is scheduled regardless. */
    @Test
    fun `reschedules even when the reminder is switched off`() = runTest {
        val stored = buildCourse(days = listOf(DayOfWeek.MONDAY)).copy(hasReminder = true)
        val edited = stored.copy(hasReminder = false)
        coEvery { repository.getCourseById(stored.id) } returns stored

        updateCourse(edited)

        verify(exactly = 1) { alarmScheduler.scheduleForCourse(edited) }
    }

    /** A course deleted underneath the editor must not blow up the save path. */
    @Test
    fun `still persists when the stored course has gone`() = runTest {
        val edited = buildCourse(days = listOf(DayOfWeek.MONDAY))
        coEvery { repository.getCourseById(edited.id) } returns null

        updateCourse(edited)

        verify(exactly = 0) { alarmScheduler.cancelForCourse(any()) }
        coVerify(exactly = 1) { repository.updateCourse(edited) }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildCourse(days: List<DayOfWeek>) = Course(
        id = 7L,
        name = "Databases",
        code = "CS-201",
        scheduleDays = days,
        classHour = 9,
        classMinute = 30,
        classDurationMinutes = 60,
        hasReminder = false,
        reminderMinutesBefore = 30,
        createdAt = 1_758_000_000_000L,
    )
}
