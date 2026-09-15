package com.abrarshakhi.selfattention.presentation.home

import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats

data class HomeUiState(
    val subjects: List<Subject> = emptyList(),
    val statsMap: Map<Long, SubjectStats> = emptyMap(),
    val overallStats: OverallStats = OverallStats(0, 0, 0f),
    val nextClass: NextClass? = null,
    val isLoading: Boolean = true,
)
