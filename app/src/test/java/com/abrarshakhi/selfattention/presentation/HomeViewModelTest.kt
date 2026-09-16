package com.abrarshakhi.selfattention.presentation

import app.cash.turbine.test
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetNextClassUseCase
import com.abrarshakhi.selfattention.domain.usecase.attendance.GetSubjectStatsUseCase
import com.abrarshakhi.selfattention.domain.usecase.subject.GetSubjectsUseCase
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
    private val getSubjects: GetSubjectsUseCase = mockk()
    private val getSubjectStats: GetSubjectStatsUseCase = mockk()
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
     * Initial isLoading should be true; after subjects are emitted it should
     * become false.
     */
    @Test
    fun `isLoading becomes false after subjects are emitted`() = runTest {
        every { getSubjects() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(getSubjects, getSubjectStats, getNextClass)

        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * When subjects are emitted, the subjects list in state should match.
     */
    @Test
    fun `subjects list is populated from use case`() = runTest {
        val subjects = listOf(buildSubject(1L, "Maths"), buildSubject(2L, "Physics"))
        every { getSubjects() } returns flowOf(subjects)
        every { getSubjectStats(any()) } returns flowOf(buildStats())

        val viewModel = HomeViewModel(getSubjects, getSubjectStats, getNextClass)

        viewModel.state.test {
            skipItems(1) // loading state
            val loaded = awaitItem()
            assert(loaded.subjects.size == 2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun buildSubject(id: Long = 1L, name: String = "Test") = Subject(
        id = id, name = name, code = "T-101",
        scheduleDays = listOf(DayOfWeek.MONDAY),
        classHour = 10, classMinute = 0,
    )

    private fun buildStats(id: Long = 1L) = SubjectStats(
        subjectId = id, totalScheduled = 0, present = 0, absent = 0, holiday = 0,
    )
}
