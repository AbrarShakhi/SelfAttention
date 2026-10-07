package com.abrarshakhi.selfattention.feature.home

import app.cash.turbine.test
import com.abrarshakhi.selfattention.core.common.time.TimeTicker
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.domain.attendance.GetCourseStatsUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.GetScheduleForDateUseCase
import com.abrarshakhi.selfattention.core.domain.attendance.MarkAttendanceUseCase
import com.abrarshakhi.selfattention.core.domain.course.GetCoursesUseCase
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCourses: GetCoursesUseCase = mockk()
    private val getCourseStats: GetCourseStatsUseCase = mockk()
    private val getSchedule: GetScheduleForDateUseCase = mockk {
        every { this@mockk.invoke(any()) } returns flowOf(emptyList())
    }
    private val markAttendance: MarkAttendanceUseCase = mockk()
    private val settingsRepository: SettingsRepository = mockk {
        every { getSettings() } returns flowOf(AppSettings())
    }
    private val fixedTicker = object : TimeTicker {
        override val minutes: Flow<LocalDateTime> = flowOf(LocalDateTime.of(2026, 10, 7, 9, 0))
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

        createViewModel().state.test {
            assertTrue(awaitItem().isLoading)
            assertFalse(awaitItem().isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `courses list is populated from use case`() = runTest {
        val courses = listOf(buildCourse(1L, "Maths"), buildCourse(2L, "Physics"))
        every { getCourses() } returns flowOf(courses)
        every { getCourseStats(any()) } returns flowOf(buildStats())

        createViewModel().state.test {
            skipItems(1)
            assertEquals(2, awaitItem().courses.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `overall stats aggregate every course`() = runTest {
        val courses = listOf(buildCourse(1L), buildCourse(2L))
        every { getCourses() } returns flowOf(courses)
        every { getCourseStats(courses[0]) } returns flowOf(buildStats(1L, present = 3, absent = 1))
        every { getCourseStats(courses[1]) } returns flowOf(buildStats(2L, present = 1, absent = 3))

        createViewModel().state.test {
            skipItems(1)
            val overall = awaitItem().overall
            assertEquals(4, overall.totalPresent)
            assertEquals(4, overall.totalAbsent)
            assertEquals(0.5f, overall.attendancePercentage, 0.0001f)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = HomeViewModel(
        getCourses = getCourses,
        getCourseStats = getCourseStats,
        getNextClass = GetNextClassUseCase(),
        getSchedule = getSchedule,
        markAttendance = markAttendance,
        settingsRepository = settingsRepository,
        timeTicker = fixedTicker,
    )

    private fun buildCourse(id: Long = 1L, name: String = "Test") = Course(
        id = id, name = name, code = "T-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10, classMinute = 0,
    )

    private fun buildStats(id: Long = 1L, present: Int = 0, absent: Int = 0) = CourseStats(
        courseId = id, totalScheduled = 0, present = present, absent = absent, holiday = 0,
    )
}
