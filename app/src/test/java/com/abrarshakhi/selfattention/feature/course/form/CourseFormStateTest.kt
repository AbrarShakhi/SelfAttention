package com.abrarshakhi.selfattention.feature.course.form

import com.abrarshakhi.selfattention.core.model.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime

class CourseFormStateTest {

    @Test
    fun `cannot save without a name and at least one day`() {
        val empty = CourseFormState()
        assertFalse(empty.canSave)
        assertFalse(empty.reduce(CourseFormEvent.NameChanged("Maths")).canSave)
        assertTrue(
            empty.reduce(CourseFormEvent.NameChanged("Maths"))
                .reduce(CourseFormEvent.DayToggled(DayOfWeek.MONDAY))
                .canSave,
        )
    }

    @Test
    fun `toggling a day twice removes it`() {
        val state = CourseFormState()
            .reduce(CourseFormEvent.DayToggled(DayOfWeek.FRIDAY))
            .reduce(CourseFormEvent.DayToggled(DayOfWeek.FRIDAY))
        assertTrue(state.days.isEmpty())
    }

    @Test
    fun `new course is trimmed and its days are sorted`() {
        val course = CourseFormState(
            name = "  Databases ",
            code = " CS-201 ",
            days = setOf(DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY),
            classTime = LocalTime.of(9, 30),
            durationMinutes = 90,
        ).toNewCourse()

        assertEquals("Databases", course.name)
        assertEquals("CS-201", course.code)
        assertEquals(listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), course.scheduleDays)
        assertEquals(9, course.classHour)
        assertEquals(30, course.classMinute)
        assertEquals(90, course.classDurationMinutes)
    }

    @Test
    fun `editing keeps the identity and creation time of the course`() {
        val original = Course(
            id = 7, name = "Old", code = "", scheduleDays = listOf(DayOfWeek.MONDAY),
            classHour = 8, classMinute = 0, createdAt = 1_000L,
        )
        val edited = CourseFormState.from(original)
            .reduce(CourseFormEvent.NameChanged("New"))
            .applyTo(original)

        assertEquals(7, edited.id)
        assertEquals(1_000L, edited.createdAt)
        assertEquals("New", edited.name)
    }
}
