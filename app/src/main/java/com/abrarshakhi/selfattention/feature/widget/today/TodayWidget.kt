package com.abrarshakhi.selfattention.feature.widget.today

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import com.abrarshakhi.selfattention.feature.widget.common.CourseBadge
import com.abrarshakhi.selfattention.feature.widget.common.LocalWidgetStatusColors
import com.abrarshakhi.selfattention.feature.widget.common.SelfAttentionWidgetTheme
import com.abrarshakhi.selfattention.feature.widget.common.WidgetPreviews
import com.abrarshakhi.selfattention.feature.widget.common.WidgetText
import com.abrarshakhi.selfattention.feature.widget.common.widgetDependencies
import com.abrarshakhi.selfattention.feature.widget.common.widgetLocale
import com.abrarshakhi.selfattention.navigation.CourseDeepLink
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TodayWidgetState(
    val settings: AppSettings,
    val date: LocalDate,
    val classes: List<ScheduledClass>,
    val now: LocalDateTime,
)

class TodayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val states = states(context, LocalDate.now())
        val initial = states.first()
        provideContent {
            val state by states.collectAsState(initial)
            SelfAttentionWidgetTheme(state.settings) { TodayContent(state) }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val today = LocalDate.now()
        val state = TodayWidgetState(AppSettings(), today, WidgetPreviews.today(today), today.atTime(12, 0))
        provideContent { SelfAttentionWidgetTheme(state.settings) { TodayContent(state) } }
    }

    private fun states(context: Context, date: LocalDate): Flow<TodayWidgetState> {
        val dependencies = context.widgetDependencies()
        return combine(
            dependencies.getSchedule()(date),
            dependencies.settingsRepository().getSettings(),
        ) { classes, settings -> TodayWidgetState(settings, date, classes, LocalDateTime.now()) }
    }
}

@Composable
private fun TodayContent(state: TodayWidgetState) {
    val title = state.date.format(DateTimeFormatter.ofPattern("EEEE, d MMM", widgetLocale()))
    Scaffold(
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(R.drawable.ic_widget_today),
                iconColor = GlanceTheme.colors.primary,
                title = title,
            )
        },
        modifier = GlanceModifier.cornerRadius(28.dp),
    ) {
        if (state.classes.isEmpty()) {
            EmptyToday()
        } else {
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                items(state.classes, itemId = { it.course.id }) { scheduled ->
                    Column {
                        ClassRow(scheduled = scheduled, now = state.now)
                        Spacer(GlanceModifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassRow(scheduled: ScheduledClass, now: LocalDateTime) {
    val context = LocalContext.current
    val hasStarted = !now.isBefore(scheduled.startsAt)
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(20.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable(actionStartActivity(CourseDeepLink.intent(context, scheduled.course.id))),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CourseBadge(course = scheduled.course, size = 36.dp)
        Spacer(GlanceModifier.width(10.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            WidgetText(scheduled.course.name, size = 14.sp, bold = true)
            WidgetText(
                "${scheduled.startsAt.clockLabel()} – ${scheduled.endsAt.clockLabel()}",
                size = 12.sp,
                color = GlanceTheme.colors.onSurfaceVariant,
            )
        }
        if (hasStarted) {
            AttendanceStatus.entries.forEach { status ->
                Spacer(GlanceModifier.width(4.dp))
                StatusButton(scheduled = scheduled, status = status)
            }
        } else {
            WidgetText("Later", size = 12.sp, color = GlanceTheme.colors.primary, bold = true)
        }
    }
}

@Composable
private fun StatusButton(scheduled: ScheduledClass, status: AttendanceStatus) {
    val palette = LocalWidgetStatusColors.current.forStatus(status)
    val selected = scheduled.status == status
    CircleIconButton(
        imageProvider = ImageProvider(status.iconRes),
        contentDescription = status.name.lowercase(),
        onClick = actionRunCallback<MarkAttendanceAction>(
            MarkAttendanceAction.parameters(scheduled.course.id, scheduled.date, if (selected) null else status),
        ),
        modifier = GlanceModifier.size(36.dp),
        backgroundColor = if (selected) palette.color else GlanceTheme.colors.surface,
        contentColor = if (selected) palette.onColor else GlanceTheme.colors.onSurfaceVariant,
    )
}

@Composable
private fun EmptyToday() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_widget_sun),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.tertiary),
            modifier = GlanceModifier.size(36.dp),
        )
        Spacer(GlanceModifier.height(6.dp))
        WidgetText("No classes today", size = 14.sp, bold = true)
        WidgetText("Enjoy the free time", size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
    }
}

private val AttendanceStatus.iconRes: Int
    get() = when (this) {
        AttendanceStatus.PRESENT -> R.drawable.ic_widget_check
        AttendanceStatus.ABSENT -> R.drawable.ic_widget_close
        AttendanceStatus.HOLIDAY -> R.drawable.ic_widget_sun
    }
