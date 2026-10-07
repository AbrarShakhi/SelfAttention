package com.abrarshakhi.selfattention.core.model

import java.time.LocalDateTime

data class NextClass(
    val course: Course,
    val scheduledAt: LocalDateTime,
)
