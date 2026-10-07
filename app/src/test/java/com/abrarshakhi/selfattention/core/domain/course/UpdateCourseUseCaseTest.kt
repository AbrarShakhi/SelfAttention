package com.abrarshakhi.selfattention.core.domain.course

import com.abrarshakhi.selfattention.core.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.model.Course
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.DayOfWeek

class UpdateCourseUseCaseTest {

    private val repository: CourseRepository = mockk(relaxed = true)
    private val alarmScheduler: AlarmScheduler = mockk(relaxed = true)
    private val updateCourse = UpdateCourseUseCase(repository, alarmScheduler)

    @Test
    fun `cancels alarms using the stored course, not the edited one`() = runTest {
        val stored = buildCourse(days = listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        val edited = stored.copy(scheduleDays = listOf(DayOfWeek.MONDAY))
        coEvery { repository.getCourseById(stored.id) } returns stored

        updateCourse(edited)

        verify(exactly = 1) { alarmScheduler.cancelForCourse(stored) }
        verify(exactly = 0) { alarmScheduler.cancelForCourse(edited) }
    }

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

    @Test
    fun `reschedules even when the reminder is switched off`() = runTest {
        val stored = buildCourse(days = listOf(DayOfWeek.MONDAY)).copy(hasReminder = true)
        val edited = stored.copy(hasReminder = false)
        coEvery { repository.getCourseById(stored.id) } returns stored

        updateCourse(edited)

        verify(exactly = 1) { alarmScheduler.scheduleForCourse(edited) }
    }

    @Test
    fun `still persists when the stored course has gone`() = runTest {
        val edited = buildCourse(days = listOf(DayOfWeek.MONDAY))
        coEvery { repository.getCourseById(edited.id) } returns null

        updateCourse(edited)

        verify(exactly = 0) { alarmScheduler.cancelForCourse(any()) }
        coVerify(exactly = 1) { repository.updateCourse(edited) }
    }

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
