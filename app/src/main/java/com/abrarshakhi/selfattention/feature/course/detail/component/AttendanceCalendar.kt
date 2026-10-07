package com.abrarshakhi.selfattention.feature.course.detail.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.forStatus
import com.abrarshakhi.selfattention.core.model.AttendanceRecord
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.ui.attendance.label
import com.abrarshakhi.selfattention.core.ui.calendar.leadingBlankCount
import com.abrarshakhi.selfattention.core.ui.calendar.weekdayOrder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceCalendar(
    month: YearMonth,
    weekStart: DayOfWeek,
    records: Map<LocalDate, AttendanceRecord>,
    isClassDay: (LocalDate) -> Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val today = remember { LocalDate.now() }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(onClick = onPreviousMonth, shapes = IconButtonDefaults.shapes()) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Previous month")
                }
                AnimatedContent(
                    targetState = month,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        (slideInHorizontally { direction * it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -direction * it / 3 } + fadeOut())
                    },
                    label = "calendarMonth",
                ) { shown ->
                    Text(
                        text = "${shown.month.getDisplayName(TextStyle.FULL, locale)} ${shown.year}",
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                FilledTonalIconButton(onClick = onNextMonth, shapes = IconButtonDefaults.shapes()) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Next month")
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayOrder(weekStart).forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.NARROW, locale),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            val firstOfMonth = month.atDay(1)
            val leading = leadingBlankCount(firstOfMonth, weekStart)
            val rows = (leading + month.lengthOfMonth() + 6) / 7
            repeat(rows) { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { column ->
                        val dayOfMonth = row * 7 + column - leading + 1
                        if (dayOfMonth in 1..month.lengthOfMonth()) {
                            val date = month.atDay(dayOfMonth)
                            DayCell(
                                date = date,
                                isClassDay = isClassDay(date),
                                isToday = date == today,
                                status = records[date]?.status,
                                onClick = { onDayClick(date) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            Legend(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DayCell(
    date: LocalDate,
    isClassDay: Boolean,
    isToday: Boolean,
    status: AttendanceStatus?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = status?.let { AppTheme.status.forStatus(it) }
    val shape = status?.polygon()?.toShape() ?: CircleShape
    val scale by animateFloatAsState(
        targetValue = if (status != null) 1f else 0.82f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "dayScale",
    )
    val description = buildString {
        append(date.dayOfMonth)
        if (isClassDay) append(", class day")
        status?.let { append(", ${it.label}") }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .semantics { contentDescription = description }
            .clip(CircleShape)
            .clickable(enabled = isClassDay, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isClassDay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .background(palette?.color ?: Color.Transparent, shape)
                    .then(
                        if (palette == null) {
                            Modifier.border(
                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                CircleShape,
                            )
                        } else {
                            Modifier
                        },
                    ),
            )
        }
        if (isToday) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday || status != null) FontWeight.Bold else FontWeight.Normal,
            color = when {
                palette != null -> palette.onColor
                isClassDay -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Legend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AttendanceStatus.entries.forEach { status ->
            val palette = AppTheme.status.forStatus(status)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(12.dp).background(palette.color, status.polygon().toShape()))
                Text(status.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
