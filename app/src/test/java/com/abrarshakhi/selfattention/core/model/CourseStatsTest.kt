package com.abrarshakhi.selfattention.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseStatsTest {

    @Test
    fun `no marked classes needs nothing and allows nothing`() {
        val stats = stats(present = 0, absent = 0)
        assertEquals(0, stats.classesToReach())
        assertEquals(0, stats.classesYouCanMiss())
        assertFalse(stats.isAtRisk)
    }

    @Test
    fun `below target reports how many classes bring it back`() {
        val stats = stats(present = 2, absent = 2)
        assertTrue(stats.isAtRisk)
        assertEquals(4, stats.classesToReach())
    }

    @Test
    fun `above target reports how many classes can be missed`() {
        val stats = stats(present = 6, absent = 0)
        assertEquals(0, stats.classesToReach())
        assertEquals(2, stats.classesYouCanMiss())
    }

    @Test
    fun `exactly on target can miss none`() {
        val stats = stats(present = 3, absent = 1)
        assertFalse(stats.isAtRisk)
        assertEquals(0, stats.classesYouCanMiss())
        assertEquals(0, stats.classesToReach())
    }

    @Test
    fun `holidays are excluded from the percentage`() {
        val stats = CourseStats(courseId = 1, totalScheduled = 10, present = 3, absent = 1, holiday = 5)
        assertEquals(0.75f, stats.attendancePercentage, 0.0001f)
    }

    private fun stats(present: Int, absent: Int) =
        CourseStats(courseId = 1, totalScheduled = present + absent, present = present, absent = absent, holiday = 0)
}
