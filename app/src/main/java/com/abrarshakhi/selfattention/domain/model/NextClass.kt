package com.abrarshakhi.selfattention.domain.model

import java.time.LocalDateTime

data class NextClass(
    val subject: Subject,
    val scheduledAt: LocalDateTime,
)
