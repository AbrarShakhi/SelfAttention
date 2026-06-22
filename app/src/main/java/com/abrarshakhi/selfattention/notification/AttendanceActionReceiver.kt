package com.abrarshakhi.selfattention.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceActionReceiver : BroadcastReceiver() {

    @Inject lateinit var attendanceRepository: AttendanceRepository

    companion object {
        const val EXTRA_SUBJECT_ID = "subject_id"
        const val EXTRA_EPOCH_DAY = "epoch_day"
        const val EXTRA_STATUS = "status"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, -1L)
        val epochDay = intent.getLongExtra(EXTRA_EPOCH_DAY, -1L)
        val statusName = intent.getStringExtra(EXTRA_STATUS) ?: return
        if (subjectId == -1L || epochDay == -1L) return

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val status = AttendanceStatus.valueOf(statusName)
                val date = LocalDate.ofEpochDay(epochDay)
                attendanceRepository.upsertRecord(subjectId, date, status)
                NotificationHelper.cancelMarkAttendancePrompt(context, subjectId)
            } finally {
                result.finish()
            }
        }
    }
}
