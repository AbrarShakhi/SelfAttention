package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
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
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class GetCourseStatsUseCaseTest {

    private val repository: AttendanceRepository = mockk()

    private val settingsRepository: SettingsRepository = mockk {
        every { getSettings() } returns flowOf(AppSettings())
    }
    private val useCase = GetCourseStatsUseCase(repository, settingsRepository)

    @Test
    fun `returns zero stats when no records exist`() = runTest {
        every { repository.getAttendanceForCourse(1L) } returns flowOf(emptyList())
        val stats = useCase(buildCourse()).first()
        assertEquals(0, stats.present)
        assertEquals(0, stats.absent)
        assertEquals(0f, stats.attendancePercentage)
    }

    @Test
    fun `returns 100 percent when all records are PRESENT`() = runTest {
        val records = listOf(
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.HOLIDAY),
        )
        every { repository.getAttendanceForCourse(1L) } returns flowOf(records)
        val stats = useCase(buildCourse()).first()
        assertEquals(2, stats.present)
        assertEquals(0, stats.absent)
        assertEquals(1.0f, stats.attendancePercentage)
    }

    @Test
    fun `returns 50 percent when present equals absent`() = runTest {
        val records = listOf(
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.ABSENT),
        )
        every { repository.getAttendanceForCourse(1L) } returns flowOf(records)
        val stats = useCase(buildCourse()).first()
        assertEquals(0.5f, stats.attendancePercentage)
    }

    private fun buildCourse() = Course(
        id = 1L,
        name = "Math",
        code = "MA-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10,
        classMinute = 0,
        createdAt = System.currentTimeMillis() - 7L * 24 * 3600 * 1000,
    )

    private fun record(status: AttendanceStatus) = AttendanceRecord(
        courseId = 1L,
        date = LocalDate.now(),
        status = status,
    )
}
