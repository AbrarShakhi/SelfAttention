package com.abrarshakhi.selfattention.domain.model

data class SubjectStats(
    val subjectId: Long,
    val totalScheduled: Int,
    val present: Int,
    val absent: Int,
    val holiday: Int,
) {
    val attendancePercentage: Float
        get() {
            val countable = present + absent
            return if (countable == 0) 0f else present.toFloat() / countable
        }
}

data class OverallStats(
    val totalPresent: Int,
    val totalAbsent: Int,
    val attendancePercentage: Float,
)
