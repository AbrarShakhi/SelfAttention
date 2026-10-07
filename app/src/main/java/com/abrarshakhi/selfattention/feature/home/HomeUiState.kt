package com.abrarshakhi.selfattention.feature.home

import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.model.NextClass
import com.abrarshakhi.selfattention.core.model.OverallStats

data class HomeUiState(
    val courses: List<Course> = emptyList(),
    val statsMap: Map<Long, CourseStats> = emptyMap(),
    val overallStats: OverallStats = OverallStats(0, 0, 0f),
    val nextClass: NextClass? = null,
    val isLoading: Boolean = true,
)
