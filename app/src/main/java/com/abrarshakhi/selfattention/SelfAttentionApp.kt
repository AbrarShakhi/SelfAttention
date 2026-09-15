package com.abrarshakhi.selfattention

import android.app.Application
import com.abrarshakhi.selfattention.notification.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SelfAttentionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
    }
}