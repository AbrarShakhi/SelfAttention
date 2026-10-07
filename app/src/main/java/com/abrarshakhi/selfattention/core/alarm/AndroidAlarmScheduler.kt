package com.abrarshakhi.selfattention.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.abrarshakhi.selfattention.core.model.Course
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) : AlarmScheduler {

    override fun scheduleForCourse(course: Course) {
        course.scheduleDays.forEach { day ->
            if (course.hasReminder) scheduleNext(course, day, AlarmType.PRE_CLASS)
            scheduleNext(course, day, AlarmType.POST_CLASS)
        }
    }

    override fun scheduleNext(course: Course, dayOfWeek: DayOfWeek, type: AlarmType) {
        val trigger = nextAlarmTrigger(course, dayOfWeek, type, LocalDateTime.now())
        val triggerAtMillis = trigger.triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val operation = pendingIntent(course.id, dayOfWeek, type, AlarmReceiver.intent(context, course.id, type, trigger.classDate))

        if (canScheduleExact()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
        }
    }

    override fun cancelForCourse(course: Course) {
        course.scheduleDays.forEach { day ->
            AlarmType.entries.forEach { type ->
                alarmManager.cancel(pendingIntent(course.id, day, type, AlarmReceiver.intent(context, course.id, type)))
            }
        }
    }

    override fun rescheduleAll(courses: List<Course>) {
        courses.forEach(::scheduleForCourse)
    }

    private fun pendingIntent(
        courseId: Long,
        dayOfWeek: DayOfWeek,
        type: AlarmType,
        intent: Intent,
    ): PendingIntent {
        val requestCode = (courseId * REQUEST_CODES_PER_COURSE + dayOfWeek.value * 2 + type.ordinal).toInt()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun canScheduleExact(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    private companion object {
        const val REQUEST_CODES_PER_COURSE = 14
    }
}
