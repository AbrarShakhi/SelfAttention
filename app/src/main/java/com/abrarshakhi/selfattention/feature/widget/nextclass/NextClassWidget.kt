package com.abrarshakhi.selfattention.feature.widget.nextclass

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.abrarshakhi.selfattention.MainActivity
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.NextClass
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import com.abrarshakhi.selfattention.core.ui.format.relativeDayLabel
import com.abrarshakhi.selfattention.feature.widget.common.CourseBadge
import com.abrarshakhi.selfattention.feature.widget.common.SelfAttentionWidgetTheme
import com.abrarshakhi.selfattention.feature.widget.common.WidgetContainer
import com.abrarshakhi.selfattention.feature.widget.common.WidgetPreviews
import com.abrarshakhi.selfattention.feature.widget.common.WidgetText
import com.abrarshakhi.selfattention.feature.widget.common.widgetDependencies
import com.abrarshakhi.selfattention.feature.widget.common.widgetLocale
import com.abrarshakhi.selfattention.navigation.CourseDeepLink
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

data class NextClassWidgetState(
    val settings: AppSettings,
    val nextClass: NextClass?,
    val today: LocalDate,
)

class NextClassWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(Compact, Wide))
    override val previewSizeMode = SizeMode.Responsive(setOf(Compact, Wide))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val states = states(context)
        val initial = states.first()
        provideContent {
            val state by states.collectAsState(initial)
            SelfAttentionWidgetTheme(state.settings) { NextClassContent(state) }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val sample = WidgetPreviews.courses.first()
        val state = NextClassWidgetState(
            settings = AppSettings(),
            nextClass = NextClass(sample, LocalDate.now().atTime(LocalTime.of(9, 30))),
            today = LocalDate.now(),
        )
        provideContent { SelfAttentionWidgetTheme(state.settings) { NextClassContent(state) } }
    }

    private fun states(context: Context): Flow<NextClassWidgetState> {
        val dependencies = context.widgetDependencies()
        val getNextClass = dependencies.getNextClass()
        return combine(
            dependencies.getCourses()(),
            dependencies.settingsRepository().getSettings(),
        ) { courses, settings ->
            NextClassWidgetState(
                settings = settings,
                nextClass = getNextClass(courses, settings.weeklyHolidays),
                today = LocalDate.now(),
            )
        }
    }

    private companion object {
        val Compact = DpSize(110.dp, 110.dp)
        val Wide = DpSize(220.dp, 110.dp)
    }
}

@Composable
private fun NextClassContent(state: NextClassWidgetState) {
    val context = LocalContext.current
    val next = state.nextClass
    val intent = next?.let { CourseDeepLink.intent(context, it.course.id) }
        ?: Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    WidgetContainer(modifier = GlanceModifier.clickable(actionStartActivity(intent))) {
        when {
            next == null -> EmptyNextClass()
            LocalSize.current.width >= 220.dp -> WideNextClass(next, state.today)
            else -> CompactNextClass(next, state.today)
        }
    }
}

@Composable
private fun CompactNextClass(next: NextClass, today: LocalDate) {
    Column(modifier = GlanceModifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CourseBadge(course = next.course, size = 36.dp)
            Spacer(GlanceModifier.width(8.dp))
            WidgetText("Up next", size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
        }
        Spacer(GlanceModifier.defaultWeight())
        WidgetText(next.course.name, size = 16.sp, bold = true, maxLines = 2)
        WidgetText(next.timeLabel(today, widgetLocale()), size = 13.sp, color = GlanceTheme.colors.primary, bold = true)
    }
}

@Composable
private fun WideNextClass(next: NextClass, today: LocalDate) {
    Row(modifier = GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        CourseBadge(course = next.course, size = 64.dp)
        Spacer(GlanceModifier.width(14.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            WidgetText("Up next", size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
            WidgetText(next.course.name, size = 18.sp, bold = true, maxLines = 2)
            Spacer(GlanceModifier.height(2.dp))
            WidgetText(next.timeLabel(today, widgetLocale()), size = 14.sp, color = GlanceTheme.colors.primary, bold = true)
            if (next.course.code.isNotBlank()) {
                WidgetText(next.course.code, size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EmptyNextClass() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_school),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
            modifier = GlanceModifier.size(32.dp),
        )
        Spacer(GlanceModifier.height(6.dp))
        WidgetText("No classes coming up", size = 13.sp, color = GlanceTheme.colors.onSurfaceVariant, maxLines = 2)
    }
}

private fun NextClass.timeLabel(today: LocalDate, locale: Locale): String {
    val day = scheduledAt.toLocalDate().relativeDayLabel(today, locale)
    return "$day · ${scheduledAt.clockLabel()}"
}
