package com.abrarshakhi.selfattention.domain.usecase.subject

import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import javax.inject.Inject

class AddSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository,
    private val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(subject: Subject) {
        val id = repository.insertSubject(subject)
        alarmScheduler.scheduleForSubject(subject.copy(id = id))
    }
}
