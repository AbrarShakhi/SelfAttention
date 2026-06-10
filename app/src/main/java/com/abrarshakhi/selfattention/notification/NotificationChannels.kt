package com.abrarshakhi.selfattention.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val CHANNEL_REMINDER = "class_reminder"
    const val CHANNEL_ATTENDANCE = "attendance_mark"

    fun create(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_REMINDER,
                    "Class Reminders",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply { description = "Reminder before each class starts" },
                NotificationChannel(
                    CHANNEL_ATTENDANCE,
                    "Mark Attendance",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "Prompt to mark attendance after class" },
            )
        )
    }
}
