package com.abrarshakhi.selfattention

import android.app.Application
import com.abrarshakhi.selfattention.core.notification.NotificationChannels
import com.abrarshakhi.selfattention.feature.widget.WidgetSync
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SelfAttentionApp : Application() {

    @Inject lateinit var widgetSync: WidgetSync

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
        widgetSync.start()
    }
}
