package com.abrarshakhi.selfattention.feature.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.ui.course.CourseAvatar
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import java.time.format.TextStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CourseCard(
    course: Course,
    stats: CourseStats?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val percentage = stats?.attendancePercentage ?: 0f
    val progress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "courseProgress",
    )
    val accent by animateColorAsState(
        targetValue = when {
            stats == null || stats.countable == 0 -> MaterialTheme.colorScheme.primary
            stats.isAtRisk -> AppTheme.status.absent.color
            else -> AppTheme.status.present.color
        },
        label = "courseAccent",
    )
    val schedule = course.scheduleDays.joinToString(" ") { it.getDisplayName(TextStyle.SHORT, locale) }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CourseAvatar(course = course)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOf(course.code, schedule, course.classTime.clockLabel())
                            .filter { it.isNotBlank() }
                            .joinToString("  ·  "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${(progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.headlineSmallEmphasized,
                    color = accent,
                )
            }
            LinearWavyProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            Text(
                text = stats.guidance(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun CourseStats?.guidance(): String {
    if (this == null || countable == 0) return "No classes marked yet"
    val needed = classesToReach()
    if (needed > 0) return "Attend the next $needed ${plural(needed)} to get back on track"
    val spare = classesYouCanMiss()
    return if (spare > 0) "You can miss $spare ${plural(spare)} and stay on track" else "Missing the next class would put you below the target"
}

private fun plural(count: Int) = if (count == 1) "class" else "classes"
