package com.abrarshakhi.selfattention.domain.usecase

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetSubjectStatsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Tests for [GetSubjectStatsUseCase].
 *
 * Verifies that attendance stats (present, absent, holiday, percentage) are
 * computed correctly from a list of attendance records.
 */
class GetSubjectStatsUseCaseTest {

    private val repository: AttendanceRepository = mockk()
    private val useCase = GetSubjectStatsUseCase(repository)

    /** Returns zero stats when no attendance records exist for the subject. */
    @Test
    fun `returns zero stats when no records exist`() = runTest {
        every { repository.getAttendanceForSubject(1L) } returns flowOf(emptyList())
        val stats = useCase(buildSubject()).first()
        assertEquals(0, stats.present)
        assertEquals(0, stats.absent)
        assertEquals(0f, stats.attendancePercentage)
    }

    /**
     * Returns 100% when all records are PRESENT and no ABSENT records exist.
     * Holiday records do not affect the percentage denominator.
     */
    @Test
    fun `returns 100 percent when all records are PRESENT`() = runTest {
        val records = listOf(
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.HOLIDAY),
        )
        every { repository.getAttendanceForSubject(1L) } returns flowOf(records)
        val stats = useCase(buildSubject()).first()
        assertEquals(2, stats.present)
        assertEquals(0, stats.absent)
        assertEquals(1.0f, stats.attendancePercentage)
    }

    /**
     * Returns 50% when present and absent counts are equal.
     */
    @Test
    fun `returns 50 percent when present equals absent`() = runTest {
        val records = listOf(
            record(AttendanceStatus.PRESENT),
            record(AttendanceStatus.ABSENT),
        )
        every { repository.getAttendanceForSubject(1L) } returns flowOf(records)
        val stats = useCase(buildSubject()).first()
        assertEquals(0.5f, stats.attendancePercentage)
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildSubject() = Subject(
        id = 1L,
        name = "Math",
        code = "MA-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10,
        classMinute = 0,
        createdAt = System.currentTimeMillis() - 7L * 24 * 3600 * 1000,
    )

    private fun record(status: AttendanceStatus) = AttendanceRecord(
        subjectId = 1L,
        date = LocalDate.now(),
        status = status,
    )
}
