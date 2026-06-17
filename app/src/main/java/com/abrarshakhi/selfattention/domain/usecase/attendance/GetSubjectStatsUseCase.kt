package com.abrarshakhi.selfattention.domain.usecase.attendance

import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetSubjectStatsUseCase @Inject constructor(
    private val repository: AttendanceRepository,
) {
    operator fun invoke(subject: Subject): Flow<SubjectStats> =
        repository.getAttendanceForSubject(subject.id).map { records ->
            computeStats(subject, records)
        }

    private fun computeStats(subject: Subject, records: List<AttendanceRecord>): SubjectStats {
        val today = LocalDate.now()
        val scheduledDates = generateSequence(subject.createdAt.toLocalDate()) { it.plusDays(1) }
            .takeWhile { !it.isAfter(today) }
            .filter { subject.scheduleDays.contains(it.dayOfWeek) }
            .toList()

        val present = records.count { it.status == AttendanceStatus.PRESENT }
        val absent = records.count { it.status == AttendanceStatus.ABSENT }
        val holiday = records.count { it.status == AttendanceStatus.HOLIDAY }

        return SubjectStats(
            subjectId = subject.id,
            totalScheduled = scheduledDates.size,
            present = present,
            absent = absent,
            holiday = holiday,
        )
    }

    private fun Long.toLocalDate(): LocalDate =
        LocalDate.ofEpochDay(this / (24L * 60 * 60 * 1000))
}
