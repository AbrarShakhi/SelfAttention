package com.abrarshakhi.selfattention.feature.course.detail.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.AttendanceDial
import com.abrarshakhi.selfattention.core.designsystem.component.ShapedIcon
import com.abrarshakhi.selfattention.core.designsystem.component.rememberSlowRotation
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.StatusColor
import com.abrarshakhi.selfattention.core.model.ATTENDANCE_TARGET
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.CourseStats
import com.abrarshakhi.selfattention.core.ui.attendance.icon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CourseHero(stats: CourseStats, modifier: Modifier = Modifier) {
    val backdrop = MaterialShapes.SoftBurst.toShape()
    val rotation = rememberSlowRotation(durationMillis = 36_000)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(196.dp)
                        .rotate(rotation)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f), backdrop),
                )
                AttendanceDial(
                    progress = stats.attendancePercentage,
                    isAtRisk = stats.isAtRisk,
                    size = 152.dp,
                    strokeWidth = 12.dp,
                    label = "attended",
                )
            }
            Text(
                text = stats.headline(),
                style = MaterialTheme.typography.titleMediumEmphasized,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatTile(AttendanceStatus.PRESENT, stats.present, AppTheme.status.present, Modifier.weight(1f))
                StatTile(AttendanceStatus.ABSENT, stats.absent, AppTheme.status.absent, Modifier.weight(1f))
                StatTile(AttendanceStatus.HOLIDAY, stats.holiday, AppTheme.status.holiday, Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StatTile(status: AttendanceStatus, value: Int, palette: StatusColor, modifier: Modifier = Modifier) {
    val animated by animateIntAsState(value, label = "statTile")
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = palette.colorContainer,
        contentColor = palette.onColorContainer,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ShapedIcon(
                icon = status.icon,
                polygon = status.polygon(),
                containerColor = palette.color,
                contentColor = palette.onColor,
                size = 32.dp,
                iconSize = 18.dp,
            )
            Text(text = animated.toString(), style = MaterialTheme.typography.headlineSmallEmphasized)
            Text(text = status.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun AttendanceStatus.polygon() = when (this) {
    AttendanceStatus.PRESENT -> MaterialShapes.Cookie9Sided
    AttendanceStatus.ABSENT -> MaterialShapes.Gem
    AttendanceStatus.HOLIDAY -> MaterialShapes.Sunny
}

private fun CourseStats.headline(): String {
    val target = (ATTENDANCE_TARGET * 100).roundToInt()
    if (countable == 0) return "Mark a class to start tracking"
    val needed = classesToReach()
    if (needed > 0) return "Attend the next $needed to get back above $target%"
    val spare = classesYouCanMiss()
    return if (spare > 0) "You can miss $spare and stay above $target%" else "Exactly on $target% — attend the next one"
}
