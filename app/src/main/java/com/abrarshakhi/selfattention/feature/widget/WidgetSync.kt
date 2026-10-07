package com.abrarshakhi.selfattention.feature.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.abrarshakhi.selfattention.core.common.coroutine.ApplicationScope
import com.abrarshakhi.selfattention.core.common.widget.WidgetUpdater
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetSync @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val settingsRepository: SettingsRepository,
    private val widgetUpdater: WidgetUpdater,
) {

    @OptIn(FlowPreview::class)
    fun start() {
        combine(
            courseRepository.getCourses(),
            attendanceRepository.getAllAttendance(),
            settingsRepository.getSettings(),
        ) { _, _, _ -> }
            .drop(1)
            .debounce(DEBOUNCE_MILLIS)
            .onEach { widgetUpdater.updateAll() }
            .launchIn(scope)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            scope.launch { publishPreviews() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun publishPreviews() {
        val manager = GlanceAppWidgetManager(context)
        listOf(
            NextClassWidgetReceiver::class,
            TodayWidgetReceiver::class,
            AttendanceWidgetReceiver::class,
        ).forEach { receiver ->
            runCatching { manager.setWidgetPreviews(receiver) }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 400L
    }
}
