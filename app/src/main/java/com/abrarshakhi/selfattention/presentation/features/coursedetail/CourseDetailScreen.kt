package com.abrarshakhi.selfattention.presentation.features.coursedetail

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.presentation.components.AttendanceRing
import com.abrarshakhi.selfattention.presentation.theme.Absent
import com.abrarshakhi.selfattention.presentation.theme.CaveatFamily
import com.abrarshakhi.selfattention.presentation.theme.Holiday
import com.abrarshakhi.selfattention.presentation.theme.Ink
import com.abrarshakhi.selfattention.presentation.theme.Ink2
import com.abrarshakhi.selfattention.presentation.theme.Ink3
import com.abrarshakhi.selfattention.presentation.theme.Present
import com.abrarshakhi.selfattention.presentation.theme.Today
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    subjectId: Long,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(subjectId) { viewModel.load(subjectId) }

    val subject = state.subject
    if (subject == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading…")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // Stats row
        state.stats?.let { stats ->
            StatsRow(stats = stats, modifier = Modifier.padding(16.dp))
        }

        HorizontalDivider(color = Ink3)

        // Month calendar
        MonthCalendar(
            month = state.currentMonth,
            subject = subject,
            records = state.records,
            onPrevMonth = viewModel::previousMonth,
            onNextMonth = viewModel::nextMonth,
            onDayClick = viewModel::openSheet,
            modifier = Modifier.padding(16.dp),
        )
    }

    // Bottom sheet for marking
    val sheetDate = state.sheetDate
    if (sheetDate != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val existingRecord = state.records[sheetDate]
        ModalBottomSheet(
            onDismissRequest = viewModel::closeSheet,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            AttendanceSheet(
                date = sheetDate,
                currentStatus = existingRecord?.status,
                onPresent = { viewModel.mark(AttendanceStatus.PRESENT) },
                onAbsent = { viewModel.mark(AttendanceStatus.ABSENT) },
                onHoliday = { viewModel.mark(AttendanceStatus.HOLIDAY) },
                onClear = viewModel::clear,
                onDismiss = viewModel::closeSheet,
            )
        }
    }
}

@Composable
private fun StatsRow(stats: SubjectStats, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AttendanceRing(
            progress = stats.attendancePercentage,
            size = 72.dp,
            label = "",
        )
        StatChip(label = "Present", value = stats.present, color = Present)
        StatChip(label = "Absent", value = stats.absent, color = Absent)
        StatChip(label = "Holiday", value = stats.holiday, color = Holiday)
    }
}

@Composable
private fun StatChip(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontFamily = CaveatFamily,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Ink2)
    }
}

@Composable
private fun MonthCalendar(
    month: YearMonth,
    subject: Subject,
    records: Map<LocalDate, AttendanceRecord>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPrevMonth) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            Text(
                text = "${
                    month.month.getDisplayName(
                        TextStyle.FULL,
                        LocalLocale.current.platformLocale
                    )
                } ${month.year}",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleLarge,
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach { dow ->
                Text(
                    text = dow.getDisplayName(TextStyle.NARROW, LocalLocale.current.platformLocale),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink2,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar grid
        val firstDay = month.atDay(1)
        val startOffset = (firstDay.dayOfWeek.value - 1) // Monday=0
        val daysInMonth = month.lengthOfMonth()
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - startOffset + 1
                    if (dayNum in 1..daysInMonth) {
                        val date = month.atDay(dayNum)
                        val isScheduled = subject.scheduleDays.contains(date.dayOfWeek)
                        val record = records[date]
                        val isToday = date == LocalDate.now()
                        CalendarDay(
                            day = dayNum,
                            isScheduled = isScheduled,
                            isToday = isToday,
                            record = record,
                            onClick = { if (isScheduled) onDayClick(date) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
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
    val bgColor = when {
        !isScheduled -> Color.Transparent
        record?.status == AttendanceStatus.PRESENT -> Present.copy(alpha = 0.2f)
        record?.status == AttendanceStatus.ABSENT -> Absent.copy(alpha = 0.2f)
        record?.status == AttendanceStatus.HOLIDAY -> Holiday.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    val dotColor = when (record?.status) {
        AttendanceStatus.PRESENT -> Present
        AttendanceStatus.ABSENT -> Absent
        AttendanceStatus.HOLIDAY -> Holiday
        null -> if (isScheduled) Ink3 else Color.Transparent
    }
    val textColor = when {
        isToday -> Today
        isScheduled -> Ink
        else -> Ink3
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(bgColor, CircleShape)
            .then(if (isToday) Modifier.border(1.5.dp, Today, CircleShape) else Modifier)
            .clickable(enabled = isScheduled, onClick = onClick),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
            )
            if (isScheduled) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(dotColor, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun AttendanceSheet(
    date: LocalDate,
    currentStatus: AttendanceStatus?,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onHoliday: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = date.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = Ink2,
        )
        Text(
            text = "Mark attendance",
            fontFamily = CaveatFamily,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onPresent,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Present),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                "✓  Present",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Button(
            onClick = onAbsent,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Absent),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                "✗  Absent",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Button(
            onClick = onHoliday,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Holiday),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                "☀  Holiday",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (currentStatus != null) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) { Text("Clear mark", style = MaterialTheme.typography.bodyMedium, color = Ink2) }
        }

        Spacer(Modifier.height(8.dp))
    }
}
