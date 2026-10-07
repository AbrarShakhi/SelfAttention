package com.abrarshakhi.selfattention.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import com.abrarshakhi.selfattention.core.ui.attendance.AttendanceToggleGroup
import com.abrarshakhi.selfattention.core.ui.course.CourseAvatar
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import com.abrarshakhi.selfattention.core.ui.format.countdownLabel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodayClassCard(
    scheduled: ScheduledClass,
    now: LocalDateTime,
    onMark: (AttendanceStatus?) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasStarted = !now.isBefore(scheduled.startsAt)

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CourseAvatar(course = scheduled.course, size = 44.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scheduled.course.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${scheduled.startsAt.clockLabel()} – ${scheduled.endsAt.clockLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!hasStarted) {
                    Text(
                        text = countdownLabel(now, scheduled.startsAt),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (hasStarted) {
                AttendanceToggleGroup(
                    selected = scheduled.status,
                    onSelect = onMark,
                    buttonSize = ToggleButtonSize.ExtraSmall,
                )
            }
        }
    }
}
