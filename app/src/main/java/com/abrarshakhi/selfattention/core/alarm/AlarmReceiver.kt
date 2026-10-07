package com.abrarshakhi.selfattention.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.meetsOn
import com.abrarshakhi.selfattention.core.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var courseRepository: CourseRepository
    @Inject lateinit var attendanceRepository: AttendanceRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var widgetUpdater: WidgetUpdater

    override fun onReceive(context: Context, intent: Intent) {
        val courseId = intent.getLongExtra(EXTRA_COURSE_ID, -1L)
        val type = AlarmType.entries.getOrNull(intent.getIntExtra(EXTRA_ALARM_TYPE, -1))
        val classDate = intent.getLongExtra(EXTRA_CLASS_EPOCH_DAY, Long.MIN_VALUE)
            .takeIf { it != Long.MIN_VALUE }
            ?.let(LocalDate::ofEpochDay)
        if (courseId == -1L || type == null || classDate == null) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val course = courseRepository.getCourseById(courseId) ?: return@launch
                val holidays = settingsRepository.getSettings().first().weeklyHolidays
                if (course.meetsOn(classDate, holidays)) notify(context, course, type, classDate)
                if (classDate.dayOfWeek in course.scheduleDays) {
                    alarmScheduler.scheduleNext(course, classDate.dayOfWeek, type)
                }
                widgetUpdater.updateAll()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun notify(context: Context, course: Course, type: AlarmType, classDate: LocalDate) {
        when (type) {
            AlarmType.PRE_CLASS -> NotificationHelper.showPreClassReminder(context, course.id, course.name, course.code)
            AlarmType.POST_CLASS -> {
                if (attendanceRepository.getRecordForCourseAndDate(course.id, classDate) == null) {
                    NotificationHelper.showMarkAttendancePrompt(context, course.id, course.name, course.code, classDate)
                }
            }
        }
    }

    companion object {
        private const val EXTRA_COURSE_ID = "course_id"
        private const val EXTRA_ALARM_TYPE = "alarm_type"
        private const val EXTRA_CLASS_EPOCH_DAY = "class_epoch_day"

        fun intent(context: Context, courseId: Long, type: AlarmType, classDate: LocalDate? = null): Intent =
            Intent(context, AlarmReceiver::class.java)
                .putExtra(EXTRA_COURSE_ID, courseId)
                .putExtra(EXTRA_ALARM_TYPE, type.ordinal)
                .apply { classDate?.let { putExtra(EXTRA_CLASS_EPOCH_DAY, it.toEpochDay()) } }
    }
}
