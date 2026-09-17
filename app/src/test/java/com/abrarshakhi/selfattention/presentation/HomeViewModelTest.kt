package com.abrarshakhi.selfattention.presentation

import app.cash.turbine.test
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.presentation.features.home.HomeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

/**
 * Tests for [HomeViewModel].
 *
 * Verifies that the UI state is updated correctly when the underlying use
 * cases emit data.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCourses: GetCoursesUseCase = mockk()
    private val getCourseStats: GetCourseStatsUseCase = mockk()
    private val getNextClass = GetNextClassUseCase()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Initial isLoading should be true; after courses are emitted it should
     * become false.
     */
    @Test
    fun `isLoading becomes false after courses are emitted`() = runTest {
        every { getCourses() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getCourses, getCourseStats, getNextClass)

        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * When courses are emitted, the courses list in state should match.
     */
    @Test
    fun `courses list is populated from use case`() = runTest {
        val courses = listOf(buildCourse(1L, "Maths"), buildCourse(2L, "Physics"))
        every { getCourses() } returns flowOf(courses)
        every { getCourseStats(any()) } returns flowOf(buildStats())

        val viewModel = HomeViewModel(getCourses, getCourseStats, getNextClass)

        viewModel.state.test {
            skipItems(1) // loading state
            val loaded = awaitItem()
            assert(loaded.courses.size == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildCourse(id: Long = 1L, name: String = "Test") = Course(
        id = id, name = name, code = "T-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10, classMinute = 0,
    )

    private fun buildStats(id: Long = 1L) = CourseStats(
        courseId = id, totalScheduled = 0, present = 0, absent = 0, holiday = 0,
    )
}
