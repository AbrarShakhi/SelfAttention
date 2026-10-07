package com.abrarshakhi.selfattention.core.domain.attendance

import com.abrarshakhi.selfattention.core.model.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class GetNextClassUseCaseTest {

    private val useCase = GetNextClassUseCase()

    @Test
    fun `returns null when course list is empty`() {
        val result = useCase(emptyList())
        assertNull(result)
    }

    @Test
    fun `returns null when course has no schedule days`() {
        val course = buildCourse(scheduleDays = emptyList())
        val result = useCase(listOf(course))
        assertNull(result)
    }

    @Test
    fun `returns single upcoming class scheduled today`() {
        val today = LocalDate.now()
        val course = buildCourse(
            scheduleDays = listOf(today.dayOfWeek),
            hour = 23, minute = 59,
        )
        val result = useCase(listOf(course))
        if (result != null) {
            assertEquals(course.id, result.course.id)
        }
    }

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
