package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

class MarkAttendanceUseCaseTest {

    private val repository: AttendanceRepository = mockk(relaxed = true)
    private val useCase = MarkAttendanceUseCase(repository)

    private val courseId = 1L
    private val date: LocalDate = LocalDate.of(2026, 5, 18)

    @Test
    fun `marks attendance as PRESENT via repository`() = runTest {
        useCase(courseId, date, AttendanceStatus.PRESENT)
        coVerify(exactly = 1) { repository.upsertRecord(courseId, date, AttendanceStatus.PRESENT) }
    }

    @Test
    fun `marks attendance as ABSENT via repository`() = runTest {
        useCase(courseId, date, AttendanceStatus.ABSENT)
        coVerify(exactly = 1) { repository.upsertRecord(courseId, date, AttendanceStatus.ABSENT) }
    }

    @Test
    fun `clear delegates to deleteRecord on repository`() = runTest {
        useCase.clear(courseId, date)
        coVerify(exactly = 1) { repository.deleteRecord(courseId, date) }
    }
}
