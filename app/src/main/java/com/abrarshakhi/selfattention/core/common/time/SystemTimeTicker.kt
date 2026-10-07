package com.abrarshakhi.selfattention.core.common.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SystemTimeTicker @Inject constructor() : TimeTicker {
    override val minutes: Flow<LocalDateTime> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now)
            delay(ChronoUnit.MILLIS.between(now, now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)))
        }
    }
}
