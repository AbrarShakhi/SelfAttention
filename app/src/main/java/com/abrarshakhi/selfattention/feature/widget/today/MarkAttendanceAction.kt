package com.abrarshakhi.selfattention.feature.widget.today

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.ActionCallback
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.feature.widget.common.widgetDependencies
import java.time.LocalDate

class MarkAttendanceAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val courseId = parameters[CourseId] ?: return
        val date = parameters[EpochDay]?.let(LocalDate::ofEpochDay) ?: return
        val status = parameters[Status]?.let { name -> AttendanceStatus.entries.firstOrNull { it.name == name } }
        val dependencies = context.widgetDependencies()
        val markAttendance = dependencies.markAttendance()

        if (status == null) markAttendance.clear(courseId, date) else markAttendance(courseId, date, status)
        dependencies.widgetUpdater().updateAll()
    }

    companion object {
        private val CourseId = ActionParameters.Key<Long>("course_id")
        private val EpochDay = ActionParameters.Key<Long>("epoch_day")
        private val Status = ActionParameters.Key<String>("status")
        private const val CLEAR = "CLEAR"

        fun parameters(courseId: Long, date: LocalDate, status: AttendanceStatus?): ActionParameters =
            actionParametersOf(
                CourseId to courseId,
                EpochDay to date.toEpochDay(),
                Status to (status?.name ?: CLEAR),
            )
    }
}
