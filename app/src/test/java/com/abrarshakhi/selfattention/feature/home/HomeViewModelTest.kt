package com.abrarshakhi.selfattention.feature.home

import app.cash.turbine.test
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCourses: GetCoursesUseCase = mockk()
    private val getCourseStats: GetCourseStatsUseCase = mockk()
    private val getNextClass = GetNextClassUseCase()
    private val settingsRepository: SettingsRepository = mockk {
        every { getSettings() } returns flowOf(AppSettings())
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isLoading becomes false after courses are emitted`() = runTest {
        every { getCourses() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getCourses, getCourseStats, getNextClass, settingsRepository)

        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `courses list is populated from use case`() = runTest {
        val courses = listOf(buildCourse(1L, "Maths"), buildCourse(2L, "Physics"))
        every { getCourses() } returns flowOf(courses)
        every { getCourseStats(any()) } returns flowOf(buildStats())

        val viewModel = HomeViewModel(getCourses, getCourseStats, getNextClass, settingsRepository)

        viewModel.state.test {
            skipItems(1)
            val loaded = awaitItem()
            assert(loaded.courses.size == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildCourse(id: Long = 1L, name: String = "Test") = Course(
        id = id, name = name, code = "T-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10, classMinute = 0,
    )

    private fun buildStats(id: Long = 1L) = CourseStats(
        courseId = id, totalScheduled = 0, present = 0, absent = 0, holiday = 0,
    )
}
