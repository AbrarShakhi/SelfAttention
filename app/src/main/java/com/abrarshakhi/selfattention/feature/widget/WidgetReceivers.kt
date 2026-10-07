package com.abrarshakhi.selfattention.feature.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.abrarshakhi.selfattention.feature.widget.attendance.AttendanceWidget
import com.abrarshakhi.selfattention.feature.widget.nextclass.NextClassWidget
import com.abrarshakhi.selfattention.feature.widget.today.TodayWidget

class NextClassWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextClassWidget()
}

class TodayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayWidget()
}

class AttendanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AttendanceWidget()
}
