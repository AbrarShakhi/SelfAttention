package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.Course
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class GetScheduleForDateUseCaseTest {

    private val monday = LocalDate.of(2026, 10, 5)
    private val early = course(1, DayOfWeek.MONDAY, hour = 8)
    private val late = course(2, DayOfWeek.MONDAY, hour = 14)
    private val tuesdayOnly = course(3, DayOfWeek.TUESDAY, hour = 9)

    private val courseRepository: CourseRepository = mockk {
        every { getCourses() } returns flowOf(listOf(late, tuesdayOnly, early))
    }
    private val attendanceRepository: AttendanceRepository = mockk {
        every { getAttendanceForDate(monday) } returns flowOf(
            listOf(AttendanceRecord(courseId = 2, date = monday, status = AttendanceStatus.ABSENT)),
        )
    }

    @Test
    fun `returns the day's classes in start order with their records`() = runTest {
        val schedule = useCase(AppSettings(weeklyHolidays = emptySet()))(monday).first()

        assertEquals(listOf(1L, 2L), schedule.map { it.course.id })
        assertEquals(null, schedule[0].status)
        assertEquals(AttendanceStatus.ABSENT, schedule[1].status)
    }

    @Test
    fun `a weekly holiday has no classes`() = runTest {
        val schedule = useCase(AppSettings(weeklyHolidays = setOf(DayOfWeek.MONDAY)))(monday).first()

        assertTrue(schedule.isEmpty())
    }

    private fun useCase(settings: AppSettings): GetScheduleForDateUseCase {
        val settingsRepository: SettingsRepository = mockk { every { getSettings() } returns flowOf(settings) }
        return GetScheduleForDateUseCase(courseRepository, attendanceRepository, settingsRepository)
    }

    private fun course(id: Long, day: DayOfWeek, hour: Int) = Course(
        id = id, name = "Course $id", code = "", scheduleDays = listOf(day), classHour = hour, classMinute = 0,
    )
}
