package com.abrarshakhi.selfattention.core.common.time

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TimeTicker {
    val minutes: Flow<LocalDateTime>
}
