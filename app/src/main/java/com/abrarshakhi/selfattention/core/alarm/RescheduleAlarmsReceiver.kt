package com.abrarshakhi.selfattention.core.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RescheduleAlarmsReceiver : BroadcastReceiver() {

    @Inject lateinit var courseRepository: CourseRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var widgetUpdater: WidgetUpdater

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in RescheduleActions) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                alarmScheduler.rescheduleAll(courseRepository.getCourses().first())
                widgetUpdater.updateAll()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        val RescheduleActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        )
    }
}
