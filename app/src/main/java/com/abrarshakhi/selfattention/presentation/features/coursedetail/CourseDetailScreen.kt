package com.abrarshakhi.selfattention.presentation.features.coursedetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.presentation.components.AttendanceRing
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import com.abrarshakhi.selfattention.presentation.theme.StatusColor
import com.abrarshakhi.selfattention.presentation.theme.forStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    subjectId: Long,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(subjectId) { viewModel.load(subjectId) }

    val subject = state.subject
    if (subject == null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        return
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SubjectHeader(
                subject = subject,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            )

            state.stats?.let { stats ->
                AttendanceSummaryCard(
                    stats = stats,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                StatTiles(stats = stats, modifier = Modifier.padding(horizontal = 16.dp))
            }

            MonthCalendarCard(
                month = state.currentMonth,
                subject = subject,
                records = state.records,
                onPrevMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onDayClick = viewModel::openSheet,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(Modifier.height(8.dp))
        }
    }

    val sheetDate = state.sheetDate
    if (sheetDate != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::closeSheet,
            sheetState = sheetState,
        ) {
            AttendanceSheet(
                date = sheetDate,
                currentStatus = state.records[sheetDate]?.status,
                onPresent = { viewModel.mark(AttendanceStatus.PRESENT) },
                onAbsent = { viewModel.mark(AttendanceStatus.ABSENT) },
                onHoliday = { viewModel.mark(AttendanceStatus.HOLIDAY) },
                onClear = viewModel::clear,
            )
        }
    }
}

// ── header ───────────────────────────────────────────────────────────────────

@Composable
private fun SubjectHeader(subject: Subject, modifier: Modifier = Modifier) {
    val locale = LocalLocale.current.platformLocale
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = subject.name, style = MaterialTheme.typography.headlineSmall)
        val schedule = remember(subject, locale) {
            val days = subject.scheduleDays.sorted()
                .joinToString(" · ") { it.getDisplayName(TextStyle.SHORT, locale) }
            val time = subject.classTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            listOf(subject.code, days, time).filter { it.isNotBlank() }.joinToString("  •  ")
        }
        Text(
            text = schedule,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── summary ──────────────────────────────────────────────────────────────────

@Composable
private fun AttendanceSummaryCard(stats: SubjectStats, modifier: Modifier = Modifier) {
    val countable = stats.present + stats.absent

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AttendanceRing(progress = stats.attendancePercentage, size = 84.dp, strokeWidth = 8.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Attendance", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (countable == 0) {
                        "No classes marked yet"
                    } else {
                        "${stats.present} of $countable classes attended"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (stats.holiday > 0) {
                    Text(
                        text = "${stats.holiday} marked as holiday",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTiles(stats: SubjectStats, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatTile("Present", stats.present, AppTheme.status.present, Modifier.weight(1f))
        StatTile("Absent", stats.absent, AppTheme.status.absent, Modifier.weight(1f))
        StatTile("Holiday", stats.holiday, AppTheme.status.holiday, Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(
    label: String,
    value: Int,
    family: StatusColor,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = family.colorContainer,
        contentColor = family.onColorContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = value.toString(), style = MaterialTheme.typography.headlineSmall)
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

// ── calendar ─────────────────────────────────────────────────────────────────

@Composable
private fun MonthCalendarCard(
    month: YearMonth,
    subject: Subject,
    records: Map<LocalDate, AttendanceRecord>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                    )
                }
                Text(
                    text = "${month.month.getDisplayName(TextStyle.FULL, locale)} ${month.year}",
                    style = MaterialTheme.typography.titleMedium,
                )
                IconButton(onClick = onNextMonth) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                DayOfWeek.entries.forEach { dow ->
                    Text(
                        text = dow.getDisplayName(TextStyle.NARROW, locale),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            val firstDay = month.atDay(1)
            val startOffset = firstDay.dayOfWeek.value - 1 // Monday = 0
            val daysInMonth = month.lengthOfMonth()
            val rows = (startOffset + daysInMonth + 6) / 7
            val today = remember { LocalDate.now() }

            repeat(rows) { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val dayNum = row * 7 + col - startOffset + 1
                        if (dayNum in 1..daysInMonth) {
                            val date = month.atDay(dayNum)
                            CalendarDay(
                                day = dayNum,
                                isScheduled = date.dayOfWeek in subject.scheduleDays,
                                isToday = date == today,
                                record = records[date],
                                onClick = { onDayClick(date) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            CalendarLegend(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isScheduled: Boolean,
    isToday: Boolean,
    record: AttendanceRecord?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val family = record?.status?.let { AppTheme.status.forStatus(it) }
    val container = family?.colorContainer ?: Color.Transparent
    // Content colour must match whatever fills the cell, or the number loses contrast.
    val content = when {
        family != null -> family.onColorContainer
        isScheduled -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        enabled = isScheduled,
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        shape = CircleShape,
        color = container,
        contentColor = content,
        border = if (isToday) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            )
            // Unmarked scheduled days get a hollow marker so they read as "needs attention".
            if (isScheduled) {
                Spacer(Modifier.height(2.dp))
                Surface(
                    modifier = Modifier.size(5.dp),
                    shape = CircleShape,
                    color = family?.color ?: MaterialTheme.colorScheme.outline,
                    content = {},
                )
            }
        }
    }
}

@Composable
private fun CalendarLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendDot("Present", AppTheme.status.present.color)
        LegendDot("Absent", AppTheme.status.absent.color)
        LegendDot("Holiday", AppTheme.status.holiday.color)
        LegendDot("Unmarked", MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(8.dp),
            shape = CircleShape,
            color = color,
            content = {},
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── mark-attendance sheet ────────────────────────────────────────────────────

@Composable
private fun AttendanceSheet(
    date: LocalDate,
    currentStatus: AttendanceStatus?,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onHoliday: () -> Unit,
    onClear: () -> Unit,
) {
    val locale = LocalLocale.current.platformLocale
    val formatted = remember(date, locale) {
        date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", locale))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Mark attendance", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = formatted,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))

        StatusButton(
            label = "Present",
            icon = { Icon(Icons.Default.Check, contentDescription = null) },
            family = AppTheme.status.present,
            selected = currentStatus == AttendanceStatus.PRESENT,
            onClick = onPresent,
        )
        StatusButton(
            label = "Absent",
            icon = { Icon(Icons.Default.Close, contentDescription = null) },
            family = AppTheme.status.absent,
            selected = currentStatus == AttendanceStatus.ABSENT,
            onClick = onAbsent,
        )
        StatusButton(
            label = "Holiday",
            icon = { Icon(Icons.Default.WbSunny, contentDescription = null) },
            family = AppTheme.status.holiday,
            selected = currentStatus == AttendanceStatus.HOLIDAY,
            onClick = onHoliday,
        )

        if (currentStatus != null) {
            TextButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Clear mark") }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatusButton(
    label: String,
    icon: @Composable () -> Unit,
    family: StatusColor,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (selected) {
            ButtonDefaults.buttonColors(
                containerColor = family.color,
                contentColor = family.onColor,
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = family.colorContainer,
                contentColor = family.onColorContainer,
            )
        },
    ) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}
