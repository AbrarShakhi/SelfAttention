package com.abrarshakhi.selfattention.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.AppRouteBackStackSaver
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Round-trip tests for [AppRouteBackStackSaver], the saver that keeps navigation state across
 * activity recreation and process death.
 */
class AppRouteBackStackSaverTest {

    /** A stack of argument-less routes survives save and restore unchanged. */
    @Test
    fun `restores a stack of object routes`() {
        val restored = roundTrip(stackOf(AppRoute.Home, AppRoute.Settings))

        assertEquals(listOf(AppRoute.Home, AppRoute.Settings), restored.toList())
    }

    /** The course id must survive too, otherwise the detail screen reopens on the wrong course. */
    @Test
    fun `restores route arguments`() {
        val restored = roundTrip(stackOf(AppRoute.Home, AppRoute.CourseDetails(courseId = 42L)))

        assertEquals(2, restored.size)
        assertEquals(AppRoute.CourseDetails(courseId = 42L), restored[1])
    }

    /** NavDisplay requires a non-empty stack, so an empty restore must fall back to Home. */
    @Test
    fun `falls back to Home when the saved stack is empty`() {
        val restored = AppRouteBackStackSaver.restore(emptyList<String>())

        assertEquals(listOf(AppRoute.Home), restored?.toList())
    }

    /** An unreadable entry is dropped rather than crashing the restore. */
    @Test
    fun `skips entries that cannot be decoded`() {
        val restored = AppRouteBackStackSaver.restore(listOf("{\"type\":\"nonsense\"}"))

        assertEquals(listOf(AppRoute.Home), restored?.toList())
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun stackOf(vararg routes: AppRoute): SnapshotStateList<AppRoute> =
        mutableStateListOf(*routes)

    private fun roundTrip(stack: SnapshotStateList<AppRoute>): SnapshotStateList<AppRoute> {
        val scope = SaverScope { true }
        val saved = with(AppRouteBackStackSaver) { scope.save(stack) }
        return requireNotNull(AppRouteBackStackSaver.restore(requireNotNull(saved)))
    }
}
