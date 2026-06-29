package com.abrarshakhi.selfattention.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.notification.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) : AlarmScheduler {

    companion object {
        const val EXTRA_SUBJECT_ID = "subject_id"
        const val EXTRA_ALARM_TYPE = "alarm_type"
        const val ALARM_TYPE_PRE_CLASS = 0
        const val ALARM_TYPE_POST_CLASS = 1
    }

    override fun scheduleForSubject(subject: Subject) {
        if (subject.hasReminder) {
            subject.scheduleDays.forEach { day ->
                scheduleNext(subject, day, ALARM_TYPE_PRE_CLASS)
            }
        }
        subject.scheduleDays.forEach { day ->
            scheduleNext(subject, day, ALARM_TYPE_POST_CLASS)
        }
    }

    override fun cancelForSubject(subject: Subject) {
        subject.scheduleDays.forEach { day ->
            cancel(subject.id, day, ALARM_TYPE_PRE_CLASS)
            cancel(subject.id, day, ALARM_TYPE_POST_CLASS)
        }
    }

    override fun rescheduleAll(subjects: List<Subject>) {
        subjects.forEach { scheduleForSubject(it) }
    }

    fun scheduleNext(subject: Subject, dayOfWeek: DayOfWeek, alarmType: Int) {
        if (!canScheduleExact()) return

        val now = LocalDateTime.now()
        val classTime = LocalTime.of(subject.classHour, subject.classMinute)
        val triggerTime = when (alarmType) {
            ALARM_TYPE_PRE_CLASS -> classTime.minusMinutes(subject.reminderMinutesBefore.toLong())
            else -> classTime.plusMinutes(subject.classDurationMinutes.toLong())
        }

        val nextOccurrence = findNextOccurrence(dayOfWeek, triggerTime, now)
        val epochMillis = nextOccurrence
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            epochMillis,
            buildPendingIntent(subject.id, dayOfWeek, alarmType),
        )
    }

    private fun cancel(subjectId: Long, dayOfWeek: DayOfWeek, alarmType: Int) {
        alarmManager.cancel(buildPendingIntent(subjectId, dayOfWeek, alarmType))
    }

    private fun findNextOccurrence(
        dayOfWeek: DayOfWeek,
        time: LocalTime,
        from: LocalDateTime,
    ): LocalDateTime {
        var date = from.toLocalDate()
        repeat(8) {
            if (date.dayOfWeek == dayOfWeek) {
                val candidate = LocalDateTime.of(date, time)
                if (candidate.isAfter(from)) return candidate
            }
            date = date.plusDays(1)
        }
        // Fallback: next week same day
        var fallback = from.toLocalDate()
        while (fallback.dayOfWeek != dayOfWeek) fallback = fallback.plusDays(1)
        return LocalDateTime.of(fallback.plusWeeks(1), time)
    }

    private fun buildPendingIntent(
        subjectId: Long,
        dayOfWeek: DayOfWeek,
        alarmType: Int,
    ): PendingIntent {
        val requestCode = ((subjectId * 14) + (dayOfWeek.value * 2) + alarmType).toInt()
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_SUBJECT_ID, subjectId)
            putExtra(EXTRA_ALARM_TYPE, alarmType)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun canScheduleExact(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
        else true
}
