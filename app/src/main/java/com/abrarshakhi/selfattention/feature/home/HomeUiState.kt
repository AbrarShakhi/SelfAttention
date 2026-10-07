package com.abrarshakhi.selfattention.feature.home

import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.model.NextClass
import com.abrarshakhi.selfattention.core.model.OverallStats
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import java.time.LocalDateTime

data class HomeUiState(
    val isLoading: Boolean = true,
    val now: LocalDateTime = LocalDateTime.now(),
    val overall: OverallStats = OverallStats.Empty,
    val nextClass: NextClass? = null,
    val today: List<ScheduledClass> = emptyList(),
    val courses: List<CourseSummary> = emptyList(),
)

data class CourseSummary(
    val course: Course,
    val stats: CourseStats?,
)
