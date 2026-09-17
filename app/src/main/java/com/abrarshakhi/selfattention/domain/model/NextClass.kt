package com.abrarshakhi.selfattention.domain.model

import java.time.LocalDateTime

data class NextClass(
    val course: Course,
    val scheduledAt: LocalDateTime,
)
