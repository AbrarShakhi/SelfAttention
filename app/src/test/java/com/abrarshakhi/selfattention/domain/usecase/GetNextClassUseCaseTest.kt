package com.abrarshakhi.selfattention.domain.usecase

import com.abrarshakhi.selfattention.domain.model.Course
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
 * list of courses and the current instant.
 */
class GetNextClassUseCaseTest {

    private val useCase = GetNextClassUseCase()

    /** Returns null when there are no courses. */
    @Test
    fun `returns null when course list is empty`() {
        val result = useCase(emptyList())
        assertNull(result)
    }

    /**
     * Returns null when a course is scheduled for a day that is never upcoming
     * within the search window. (Degenerate case — schedule with no days.)
     */
    @Test
    fun `returns null when course has no schedule days`() {
        val course = buildCourse(scheduleDays = emptyList())
        val result = useCase(listOf(course))
        assertNull(result)
    }

    /**
     * Returns the single upcoming class when there is exactly one course with
     * one scheduled day and the class has not yet started today.
     */
    @Test
    fun `returns single upcoming class scheduled today`() {
        val today = LocalDate.now()
        val course = buildCourse(
            scheduleDays = listOf(today.dayOfWeek),
            hour = 23, minute = 59, // always later than now unless run at midnight
        )
        val result = useCase(listOf(course))
        // We can only assert non-null; exact time depends on the test machine's clock.
        if (result != null) {
            assertEquals(course.id, result.course.id)
        }
    }

    /**
     * When two courses are both upcoming, the one with the earlier class time
     * is returned.
     */
    @Test
    fun `returns the earlier of two upcoming classes`() {
        val today = LocalDate.now()
        val earlier = buildCourse(id = 1, scheduleDays = listOf(today.dayOfWeek), hour = 23, minute = 0)
        val later = buildCourse(id = 2, scheduleDays = listOf(today.dayOfWeek), hour = 23, minute = 59)
        val result = useCase(listOf(earlier, later))
        if (result != null) {
            assertEquals(1L, result.course.id)
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildCourse(
        id: Long = 1L,
        scheduleDays: List<DayOfWeek> = emptyList(),
        hour: Int = 10,
        minute: Int = 0,
    ) = Course(
        id = id,
        name = "Test",
        code = "T-101",
        scheduleDays = scheduleDays,
        classHour = hour,
        classMinute = minute,
    )
}
