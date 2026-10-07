package com.abrarshakhi.selfattention.feature.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.feature.widget.attendance.AttendanceWidget
import com.abrarshakhi.selfattention.feature.widget.nextclass.NextClassWidget
import com.abrarshakhi.selfattention.feature.widget.today.TodayWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GlanceWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdater {

    override suspend fun updateAll() {
        NextClassWidget().updateAll(context)
        TodayWidget().updateAll(context)
        AttendanceWidget().updateAll(context)
    }
}
