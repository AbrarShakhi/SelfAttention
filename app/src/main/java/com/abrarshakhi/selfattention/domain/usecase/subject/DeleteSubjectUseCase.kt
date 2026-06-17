package com.abrarshakhi.selfattention.domain.usecase.subject

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import javax.inject.Inject

class DeleteSubjectUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(subject: Subject) {
        alarmScheduler.cancelForSubject(subject)
        attendanceRepository.deleteAllForSubject(subject.id)
        subjectRepository.deleteSubject(subject)
    }
}
