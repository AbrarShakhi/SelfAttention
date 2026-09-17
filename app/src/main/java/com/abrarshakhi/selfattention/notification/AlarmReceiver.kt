package com.abrarshakhi.selfattention.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.data.alarm.AlarmSchedulerImpl
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var courseRepository: CourseRepository
    @Inject lateinit var attendanceRepository: AttendanceRepository
    @Inject lateinit var alarmScheduler: AlarmSchedulerImpl

    override fun onReceive(context: Context, intent: Intent) {
        val courseId = intent.getLongExtra(AlarmSchedulerImpl.EXTRA_COURSE_ID, -1L)
        val alarmType = intent.getIntExtra(AlarmSchedulerImpl.EXTRA_ALARM_TYPE, -1)
        if (courseId == -1L || alarmType == -1) return

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val course = courseRepository.getCourseById(courseId) ?: return@launch
                val today = LocalDate.now()

                when (alarmType) {
                    AlarmSchedulerImpl.ALARM_TYPE_PRE_CLASS -> {
                        NotificationHelper.showPreClassReminder(
                            context, course.id, course.name, course.code,
                        )
                    }
                    AlarmSchedulerImpl.ALARM_TYPE_POST_CLASS -> {
                        val record = attendanceRepository.getRecordForCourseAndDate(courseId, today)
                        if (record == null) {
                            NotificationHelper.showMarkAttendancePrompt(
                                context, course.id, course.name, course.code, today,
                            )
                        }
                    }
                }

                // Reschedule for next week
                alarmScheduler.scheduleNext(course, today.dayOfWeek, alarmType)
            } finally {
                result.finish()
            }
        }
    }
}
