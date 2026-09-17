package com.abrarshakhi.selfattention.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.abrarshakhi.selfattention.MainActivity
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import java.time.LocalDate

object NotificationHelper {

    fun showPreClassReminder(context: Context, courseId: Long, courseName: String, code: String) {
        if (!hasPermission(context)) return
        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Class starting soon")
            .setContentText("$courseName ($code) is about to begin")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .build()
        notify(context, notificationId(courseId, 0), notification)
    }

    fun showMarkAttendancePrompt(
        context: Context,
        courseId: Long,
        courseName: String,
        code: String,
        date: LocalDate,
    ) {
        if (!hasPermission(context)) return
        val epochDay = date.toEpochDay()

        fun actionIntent(status: AttendanceStatus): PendingIntent {
            val intent = Intent(context, AttendanceActionReceiver::class.java).apply {
                putExtra(AttendanceActionReceiver.EXTRA_COURSE_ID, courseId)
                putExtra(AttendanceActionReceiver.EXTRA_EPOCH_DAY, epochDay)
                putExtra(AttendanceActionReceiver.EXTRA_STATUS, status.name)
            }
            val reqCode = (courseId * 10 + status.ordinal).toInt()
            return PendingIntent.getBroadcast(
                context, reqCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ATTENDANCE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Mark your attendance")
            .setContentText("$courseName ($code) — how was class?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppIntent(context))
            .addAction(0, "✓ Present", actionIntent(AttendanceStatus.PRESENT))
            .addAction(0, "✗ Absent", actionIntent(AttendanceStatus.ABSENT))
            .addAction(0, "☀ Holiday", actionIntent(AttendanceStatus.HOLIDAY))
            .build()

        notify(context, notificationId(courseId, 1), notification)
    }

    fun cancelMarkAttendancePrompt(context: Context, courseId: Long) {
        context.getSystemService(NotificationManager::class.java)
            .cancel(notificationId(courseId, 1))
    }

    private fun openAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun notify(context: Context, id: Int, notification: Notification) {
        context.getSystemService(NotificationManager::class.java).notify(id, notification)
    }

    private fun notificationId(courseId: Long, type: Int) = (courseId * 2 + type).toInt()

    private fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            return granted == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}
