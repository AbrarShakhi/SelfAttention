package com.abrarshakhi.selfattention.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.abrarshakhi.selfattention.core.model.Course
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
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
        if (!canScheduleExact()) return

        val triggerTime = when (type) {
            AlarmType.PRE_CLASS -> course.classTime.minusMinutes(course.reminderMinutesBefore.toLong())
            AlarmType.POST_CLASS -> course.classTime.plusMinutes(course.classDurationMinutes.toLong())
        }
        val triggerAt = nextOccurrence(dayOfWeek, triggerTime, LocalDateTime.now())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent(course.id, dayOfWeek, type),
        )
    }

    override fun cancelForCourse(course: Course) {
        course.scheduleDays.forEach { day ->
            AlarmType.entries.forEach { type ->
                alarmManager.cancel(pendingIntent(course.id, day, type))
            }
        }
    }

    override fun rescheduleAll(courses: List<Course>) {
        courses.forEach(::scheduleForCourse)
    }

    private fun nextOccurrence(
        dayOfWeek: DayOfWeek,
        time: LocalTime,
        from: LocalDateTime,
    ): LocalDateTime {
        val daysAhead = (dayOfWeek.value - from.dayOfWeek.value + DAYS_IN_WEEK) % DAYS_IN_WEEK
        val candidate = LocalDateTime.of(from.toLocalDate().plusDays(daysAhead.toLong()), time)
        return if (candidate.isAfter(from)) candidate else candidate.plusWeeks(1)
    }

    private fun pendingIntent(courseId: Long, dayOfWeek: DayOfWeek, type: AlarmType): PendingIntent {
        val requestCode = (courseId * REQUEST_CODES_PER_COURSE + dayOfWeek.value * 2 + type.ordinal).toInt()
        val intent = AlarmReceiver.intent(context, courseId, type)
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
        const val DAYS_IN_WEEK = 7
        const val REQUEST_CODES_PER_COURSE = 14
    }
}
