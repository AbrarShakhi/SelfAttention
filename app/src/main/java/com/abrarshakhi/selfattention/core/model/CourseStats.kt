package com.abrarshakhi.selfattention.core.model

import kotlin.math.ceil
import kotlin.math.floor

const val ATTENDANCE_TARGET: Float = 0.75f

data class CourseStats(
    val courseId: Long,
    val totalScheduled: Int,
    val present: Int,
    val absent: Int,
    val holiday: Int,
) {
    val countable: Int get() = present + absent

    val attendancePercentage: Float
        get() = if (countable == 0) 0f else present.toFloat() / countable

    val isAtRisk: Boolean
        get() = countable > 0 && attendancePercentage < ATTENDANCE_TARGET

    fun classesToReach(target: Float = ATTENDANCE_TARGET): Int {
        if (countable == 0 || attendancePercentage >= target) return 0
        return ceil((target * countable - present) / (1f - target)).toInt()
    }

    fun classesYouCanMiss(target: Float = ATTENDANCE_TARGET): Int {
        if (countable == 0) return 0
        return floor(present / target - countable).toInt().coerceAtLeast(0)
    }
}

data class OverallStats(
    val totalPresent: Int,
    val totalAbsent: Int,
    val attendancePercentage: Float,
) {
    val countable: Int get() = totalPresent + totalAbsent

    val isAtRisk: Boolean
        get() = countable > 0 && attendancePercentage < ATTENDANCE_TARGET

    companion object {
        val Empty = OverallStats(totalPresent = 0, totalAbsent = 0, attendancePercentage = 0f)

        fun of(stats: Collection<CourseStats>): OverallStats {
            val present = stats.sumOf { it.present }
            val absent = stats.sumOf { it.absent }
            val countable = present + absent
            return OverallStats(
                totalPresent = present,
                totalAbsent = absent,
                attendancePercentage = if (countable == 0) 0f else present.toFloat() / countable,
            )
        }
    }
}
