package com.abrarshakhi.selfattention.feature.widget.attendance

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.abrarshakhi.selfattention.MainActivity
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.model.OverallStats
import com.abrarshakhi.selfattention.feature.widget.common.LocalWidgetStatusColors
import com.abrarshakhi.selfattention.feature.widget.common.SelfAttentionWidgetTheme
import com.abrarshakhi.selfattention.feature.widget.common.WidgetContainer
import com.abrarshakhi.selfattention.feature.widget.common.WidgetPreviews
import com.abrarshakhi.selfattention.feature.widget.common.WidgetText
import com.abrarshakhi.selfattention.feature.widget.common.widgetDependencies
import com.abrarshakhi.selfattention.navigation.CourseDeepLink
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

data class CourseAttendance(val course: Course, val stats: CourseStats)

data class AttendanceWidgetState(
    val settings: AppSettings,
    val courses: List<CourseAttendance>,
) {
    val overall: OverallStats get() = OverallStats.of(courses.map { it.stats })
}

class AttendanceWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(Compact, Medium, Large))
    override val previewSizeMode = SizeMode.Responsive(setOf(Compact, Medium, Large))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val states = states(context)
        val initial = states.first()
        provideContent {
            val state by states.collectAsState(initial)
            SelfAttentionWidgetTheme(state.settings) { AttendanceContent(state) }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val state = AttendanceWidgetState(
            settings = AppSettings(),
            courses = WidgetPreviews.courses.zip(WidgetPreviews.stats, ::CourseAttendance),
        )
        provideContent { SelfAttentionWidgetTheme(state.settings) { AttendanceContent(state) } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun states(context: Context): Flow<AttendanceWidgetState> {
        val dependencies = context.widgetDependencies()
        val getStats = dependencies.getCourseStats()
        val courses = dependencies.getCourses()().flatMapLatest { list ->
            if (list.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(list.map { course -> getStats(course).map { CourseAttendance(course, it) } }) { it.toList() }
            }
        }
        return combine(dependencies.settingsRepository().getSettings(), courses, ::AttendanceWidgetState)
    }

    private companion object {
        val Compact = DpSize(110.dp, 110.dp)
        val Medium = DpSize(220.dp, 110.dp)
        val Large = DpSize(220.dp, 220.dp)
    }
}

@Composable
private fun AttendanceContent(state: AttendanceWidgetState) {
    val context = LocalContext.current
    val size = LocalSize.current
    val open = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    WidgetContainer(modifier = GlanceModifier.clickable(actionStartActivity(open))) {
        when {
            size.width < 220.dp -> Box(GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                OverallRing(state.overall, ringSize = 92.dp)
            }
            size.height < 200.dp -> Row(GlanceModifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                OverallRing(state.overall, ringSize = 84.dp)
                Spacer(GlanceModifier.width(14.dp))
                Column(GlanceModifier.defaultWeight()) {
                    state.courses.sortedBy { it.stats.attendancePercentage }.take(2).forEach {
                        CourseLine(it)
                        Spacer(GlanceModifier.height(6.dp))
                    }
                    if (state.courses.isEmpty()) {
                        WidgetText("Add a course to start tracking", size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant, maxLines = 2)
                    }
                }
            }
            else -> Column(GlanceModifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OverallRing(state.overall, ringSize = 72.dp)
                    Spacer(GlanceModifier.width(12.dp))
                    Column {
                        WidgetText("Attendance", size = 16.sp, bold = true)
                        WidgetText(state.overall.summary(), size = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
                    }
                }
                Spacer(GlanceModifier.height(10.dp))
                LazyColumn(GlanceModifier.fillMaxWidth().defaultWeight()) {
                    items(state.courses, itemId = { it.course.id }) { item ->
                        Column {
                            CourseLine(item, clickable = true)
                            Spacer(GlanceModifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverallRing(overall: OverallStats, ringSize: Dp) {
    val context = LocalContext.current
    val status = LocalWidgetStatusColors.current
    val accent = when {
        overall.countable == 0 -> GlanceTheme.colors.primary
        overall.isAtRisk -> status.absent.color
        else -> status.present.color
    }
    val density = context.resources.displayMetrics.density
    val ring = attendanceRing(
        sizePx = (ringSize.value * density).roundToInt(),
        strokePx = 9f * density,
        progress = overall.attendancePercentage,
        trackColor = GlanceTheme.colors.secondaryContainer.getColor(context).toArgb(),
        progressColor = accent.getColor(context).toArgb(),
    )
    Box(modifier = GlanceModifier.size(ringSize), contentAlignment = Alignment.Center) {
        Image(provider = ImageProvider(ring), contentDescription = null, modifier = GlanceModifier.fillMaxSize())
        WidgetText("${(overall.attendancePercentage * 100).roundToInt()}%", size = (ringSize.value / 4.6f).sp, bold = true)
    }
}

@Composable
private fun CourseLine(item: CourseAttendance, clickable: Boolean = false) {
    val context = LocalContext.current
    val status = LocalWidgetStatusColors.current
    val accent = when {
        item.stats.countable == 0 -> GlanceTheme.colors.primary
        item.stats.isAtRisk -> status.absent.color
        else -> status.present.color
    }
    val modifier = if (clickable) {
        GlanceModifier.fillMaxWidth().clickable(actionStartActivity(CourseDeepLink.intent(context, item.course.id)))
    } else {
        GlanceModifier.fillMaxWidth()
    }
    Column(modifier = modifier) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            WidgetText(item.course.name, size = 13.sp, bold = true, modifier = GlanceModifier.defaultWeight())
            WidgetText("${(item.stats.attendancePercentage * 100).roundToInt()}%", size = 13.sp, bold = true, color = accent)
        }
        Spacer(GlanceModifier.height(4.dp))
        LinearProgressIndicator(
            progress = item.stats.attendancePercentage,
            modifier = GlanceModifier.fillMaxWidth().height(6.dp).cornerRadius(3.dp),
            color = accent,
            backgroundColor = GlanceTheme.colors.secondaryContainer,
        )
    }
}

private fun OverallStats.summary(): String =
    if (countable == 0) "Nothing marked yet" else "$totalPresent of $countable classes"
