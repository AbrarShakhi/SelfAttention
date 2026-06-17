package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOverallStatsUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(): Flow<OverallStats> =
        repository.getAllAttendance().map { records ->
            val present = records.count { it.status == AttendanceStatus.PRESENT }
            val absent = records.count { it.status == AttendanceStatus.ABSENT }
            val countable = present + absent
            OverallStats(
                totalPresent = present,
                totalAbsent = absent,
                attendancePercentage = if (countable == 0) 0f else present.toFloat() / countable,
            )
        }
}
