package com.abrarshakhi.selfattention.domain.alarm

import com.abrarshakhi.selfattention.domain.model.Subject

interface AlarmScheduler {
    fun scheduleForSubject(subject: Subject)
    fun cancelForSubject(subject: Subject)
    fun rescheduleAll(subjects: List<Subject>)
}
