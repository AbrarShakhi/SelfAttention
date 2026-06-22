package com.abrarshakhi.selfattention.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.repository.SubjectRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var subjectRepository: SubjectRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != "android.intent.action.LOCKED_BOOT_COMPLETED") return

        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val subjects = subjectRepository.getSubjects().first()
                alarmScheduler.rescheduleAll(subjects)
            } finally {
                result.finish()
            }
        }
    }
}
