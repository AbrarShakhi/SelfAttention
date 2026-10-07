package com.abrarshakhi.selfattention.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var courseRepository: CourseRepository
    @Inject lateinit var attendanceRepository: AttendanceRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val courseId = intent.getLongExtra(EXTRA_COURSE_ID, -1L)
        val type = AlarmType.entries.getOrNull(intent.getIntExtra(EXTRA_ALARM_TYPE, -1))
        if (courseId == -1L || type == null) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val course = courseRepository.getCourseById(courseId) ?: return@launch
                val today = LocalDate.now()

                when (type) {
                    AlarmType.PRE_CLASS -> NotificationHelper.showPreClassReminder(
                        context, course.id, course.name, course.code,
                    )
                    AlarmType.POST_CLASS -> {
                        val record = attendanceRepository.getRecordForCourseAndDate(courseId, today)
                        if (record == null) {
                            NotificationHelper.showMarkAttendancePrompt(
                                context, course.id, course.name, course.code, today,
                            )
                        }
                    }
                }

                alarmScheduler.scheduleNext(course, today.dayOfWeek, type)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val EXTRA_COURSE_ID = "course_id"
        private const val EXTRA_ALARM_TYPE = "alarm_type"

        fun intent(context: Context, courseId: Long, type: AlarmType): Intent =
            Intent(context, AlarmReceiver::class.java)
                .putExtra(EXTRA_COURSE_ID, courseId)
                .putExtra(EXTRA_ALARM_TYPE, type.ordinal)
    }
}
