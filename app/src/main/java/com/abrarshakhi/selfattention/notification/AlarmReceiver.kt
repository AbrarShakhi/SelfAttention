package com.abrarshakhi.selfattention.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.data.alarm.AlarmSchedulerImpl
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var subjectRepository: SubjectRepository
    @Inject lateinit var attendanceRepository: AttendanceRepository
    @Inject lateinit var alarmScheduler: AlarmSchedulerImpl

    override fun onReceive(context: Context, intent: Intent) {
        val subjectId = intent.getLongExtra(AlarmSchedulerImpl.EXTRA_SUBJECT_ID, -1L)
        val alarmType = intent.getIntExtra(AlarmSchedulerImpl.EXTRA_ALARM_TYPE, -1)
        if (subjectId == -1L || alarmType == -1) return

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val subject = subjectRepository.getSubjectById(subjectId) ?: return@launch
                val today = LocalDate.now()

                when (alarmType) {
                    AlarmSchedulerImpl.ALARM_TYPE_PRE_CLASS -> {
                        NotificationHelper.showPreClassReminder(
                            context, subject.id, subject.name, subject.code,
                        )
                    }
                    AlarmSchedulerImpl.ALARM_TYPE_POST_CLASS -> {
                        val record = attendanceRepository.getRecordForSubjectAndDate(subjectId, today)
                        if (record == null) {
                            NotificationHelper.showMarkAttendancePrompt(
                                context, subject.id, subject.name, subject.code, today,
                            )
                        }
                    }
                }

                // Reschedule for next week
                alarmScheduler.scheduleNext(subject, today.dayOfWeek, alarmType)
            } finally {
                result.finish()
            }
        }
    }
}
