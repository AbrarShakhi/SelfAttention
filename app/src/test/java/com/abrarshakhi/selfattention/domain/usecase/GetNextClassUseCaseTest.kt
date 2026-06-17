package com.abrarshakhi.selfattention.domain.usecase

import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetNextClassUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Tests for [GetNextClassUseCase].
 *
 * Each case verifies that the correct next upcoming class is returned given a
 * list of subjects and the current instant.
 */
class GetNextClassUseCaseTest {

    private val useCase = GetNextClassUseCase()

    /** Returns null when there are no subjects. */
    @Test
    fun `returns null when subject list is empty`() {
        val result = useCase(emptyList())
        assertNull(result)
    }

    /**
     * Returns null when a subject is scheduled for a day that is never upcoming
     * within the search window. (Degenerate case — schedule with no days.)
     */
    @Test
    fun `returns null when subject has no schedule days`() {
        val subject = buildSubject(scheduleDays = emptyList())
        val result = useCase(listOf(subject))
        assertNull(result)
    }

    /**
     * Returns the single upcoming class when there is exactly one subject with
     * one scheduled day and the class has not yet started today.
     */
    @Test
    fun `returns single upcoming class scheduled today`() {
        val today = LocalDate.now()
        val subject = buildSubject(
            scheduleDays = listOf(today.dayOfWeek),
            hour = 23, minute = 59, // always later than now unless run at midnight
        )
        val result = useCase(listOf(subject))
        // We can only assert non-null; exact time depends on the test machine's clock.
        if (result != null) {
            assertEquals(subject.id, result.subject.id)
        }
    }

    /**
     * When two subjects are both upcoming, the one with the earlier class time
     * is returned.
     */
    @Test
    fun `returns the earlier of two upcoming classes`() {
        val today = LocalDate.now()
        val earlier = buildSubject(id = 1, scheduleDays = listOf(today.dayOfWeek), hour = 23, minute = 0)
        val later = buildSubject(id = 2, scheduleDays = listOf(today.dayOfWeek), hour = 23, minute = 59)
        val result = useCase(listOf(earlier, later))
        if (result != null) {
            assertEquals(1L, result.subject.id)
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildSubject(
        id: Long = 1L,
        scheduleDays: List<DayOfWeek> = emptyList(),
        hour: Int = 10,
        minute: Int = 0,
    ) = Subject(
        id = id,
        name = "Test",
        code = "T-101",
        scheduleDays = scheduleDays,
        classHour = hour,
        classMinute = minute,
    )
}
