package com.abrarshakhi.selfattention.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.junit.Assert.assertEquals
import org.junit.Test

class AppRouteBackStackSaverTest {

    @Test
    fun `restores a stack of object routes`() {
        val restored = roundTrip(stackOf(AppRoute.Home, AppRoute.Settings))

        assertEquals(listOf(AppRoute.Home, AppRoute.Settings), restored.toList())
    }

    @Test
    fun `restores route arguments`() {
        val restored = roundTrip(stackOf(AppRoute.Home, AppRoute.CourseDetail(courseId = 42L)))

        assertEquals(2, restored.size)
        assertEquals(AppRoute.CourseDetail(courseId = 42L), restored[1])
    }

    @Test
    fun `restores a deep stack through the course editor`() {
        val restored = roundTrip(
            stackOf(
                AppRoute.Home,
                AppRoute.CourseDetail(courseId = 7L),
                AppRoute.CourseEditor(courseId = 7L),
            ),
        )

        assertEquals(
            listOf(
                AppRoute.Home,
                AppRoute.CourseDetail(courseId = 7L),
                AppRoute.CourseEditor(courseId = 7L),
            ),
            restored.toList(),
        )
    }

    @Test
    fun `falls back to Home when the saved stack is empty`() {
        val restored = AppRouteBackStackSaver.restore(emptyList<String>())

        assertEquals(listOf(AppRoute.Home), restored?.toList())
    }

    @Test
    fun `skips entries that cannot be decoded`() {
        val restored = AppRouteBackStackSaver.restore(listOf("{\"type\":\"nonsense\"}"))

        assertEquals(listOf(AppRoute.Home), restored?.toList())
    }

    private fun stackOf(vararg routes: AppRoute): SnapshotStateList<AppRoute> =
        mutableStateListOf(*routes)

    private fun roundTrip(stack: SnapshotStateList<AppRoute>): SnapshotStateList<AppRoute> {
        val scope = SaverScope { true }
        val saved = with(AppRouteBackStackSaver) { scope.save(stack) }
        return requireNotNull(AppRouteBackStackSaver.restore(requireNotNull(saved)))
    }
}
