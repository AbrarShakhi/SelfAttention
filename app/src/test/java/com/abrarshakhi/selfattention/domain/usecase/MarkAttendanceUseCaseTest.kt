package com.abrarshakhi.selfattention.domain.usecase

import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.usecase.attendance.MarkAttendanceUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

/**
 * Tests for [MarkAttendanceUseCase].
 *
 * Verifies that the use case correctly delegates to [AttendanceRepository]
 * for both marking and clearing attendance.
 */
class MarkAttendanceUseCaseTest {

    private val repository: AttendanceRepository = mockk(relaxed = true)
    private val useCase = MarkAttendanceUseCase(repository)

    private val courseId = 1L
    private val date: LocalDate = LocalDate.of(2026, 5, 18)

    /**
     * Calling invoke with PRESENT status should call upsertRecord with PRESENT
     * on the repository.
     */
    @Test
    fun `marks attendance as PRESENT via repository`() = runTest {
        useCase(courseId, date, AttendanceStatus.PRESENT)
        coVerify(exactly = 1) { repository.upsertRecord(courseId, date, AttendanceStatus.PRESENT) }
    }

    /**
     * Calling invoke with ABSENT status should call upsertRecord with ABSENT
     * on the repository.
     */
    @Test
    fun `marks attendance as ABSENT via repository`() = runTest {
        useCase(courseId, date, AttendanceStatus.ABSENT)
        coVerify(exactly = 1) { repository.upsertRecord(courseId, date, AttendanceStatus.ABSENT) }
    }

    /**
     * Calling clear should call deleteRecord on the repository, removing any
     * existing mark for the given course and date.
     */
    @Test
    fun `clear delegates to deleteRecord on repository`() = runTest {
        useCase.clear(courseId, date)
        coVerify(exactly = 1) { repository.deleteRecord(courseId, date) }
    }
}
