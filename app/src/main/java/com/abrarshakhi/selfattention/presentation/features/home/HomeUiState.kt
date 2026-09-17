package com.abrarshakhi.selfattention.presentation.features.home

import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats

data class HomeUiState(
    val courses: List<Course> = emptyList(),
    val statsMap: Map<Long, CourseStats> = emptyMap(),
    val overallStats: OverallStats = OverallStats(0, 0, 0f),
    val nextClass: NextClass? = null,
    val isLoading: Boolean = true,
)
