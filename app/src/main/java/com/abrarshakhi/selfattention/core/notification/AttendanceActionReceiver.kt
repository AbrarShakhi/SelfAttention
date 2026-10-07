package com.abrarshakhi.selfattention.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceActionReceiver : BroadcastReceiver() {

    @Inject lateinit var attendanceRepository: AttendanceRepository
    @Inject lateinit var widgetUpdater: WidgetUpdater

    companion object {
        const val EXTRA_COURSE_ID = "course_id"
        const val EXTRA_EPOCH_DAY = "epoch_day"
        const val EXTRA_STATUS = "status"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val courseId = intent.getLongExtra(EXTRA_COURSE_ID, -1L)
        val epochDay = intent.getLongExtra(EXTRA_EPOCH_DAY, -1L)
        val statusName = intent.getStringExtra(EXTRA_STATUS) ?: return
        if (courseId == -1L || epochDay == -1L) return

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val status = AttendanceStatus.valueOf(statusName)
                val date = LocalDate.ofEpochDay(epochDay)
                attendanceRepository.upsertRecord(courseId, date, status)
                NotificationHelper.cancelMarkAttendancePrompt(context, courseId)
                widgetUpdater.updateAll()
            } finally {
                result.finish()
            }
        }
    }
}
