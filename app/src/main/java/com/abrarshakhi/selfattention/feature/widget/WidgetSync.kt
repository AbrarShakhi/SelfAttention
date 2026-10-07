package com.abrarshakhi.selfattention.feature.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
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
        val installedAt = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        val prefs = context.getSharedPreferences(PREVIEW_PREFS, Context.MODE_PRIVATE)
        if (prefs.getLong(KEY_PUBLISHED_FOR, 0L) == installedAt) return

        val manager = GlanceAppWidgetManager(context)
        val receivers = listOf(
            NextClassWidgetReceiver::class,
            TodayWidgetReceiver::class,
            AttendanceWidgetReceiver::class,
        )
        val allPublished = receivers.all { receiver ->
            runCatching { manager.setWidgetPreviews(receiver) }.getOrNull() ==
                GlanceAppWidgetManager.SET_WIDGET_PREVIEWS_RESULT_SUCCESS
        }
        if (allPublished) prefs.edit { putLong(KEY_PUBLISHED_FOR, installedAt) }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 400L
        const val PREVIEW_PREFS = "widget_previews"
        const val KEY_PUBLISHED_FOR = "published_for_install"
    }
}
