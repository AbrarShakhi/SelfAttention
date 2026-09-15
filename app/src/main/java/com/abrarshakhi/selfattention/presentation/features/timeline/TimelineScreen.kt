package com.abrarshakhi.selfattention.presentation.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.presentation.components.DayStrip
import com.abrarshakhi.selfattention.presentation.components.SectionLabel
import com.abrarshakhi.selfattention.presentation.theme.Absent
import com.abrarshakhi.selfattention.presentation.theme.CaveatFamily
import com.abrarshakhi.selfattention.presentation.theme.Holiday
import com.abrarshakhi.selfattention.presentation.theme.Ink2
import com.abrarshakhi.selfattention.presentation.theme.Ink3
import com.abrarshakhi.selfattention.presentation.theme.Present
import java.time.format.DateTimeFormatter

@Composable
fun TimelineScreen(
    onSubjectClick: (Long) -> Unit,
    viewModel: TimelineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Text(
            text = "Timeline",
            fontFamily = CaveatFamily,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        DayStrip(
            days = state.weekDays,
            selectedDay = state.selectedDay,
            onDaySelected = viewModel::selectDay,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = Ink3)

        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
        SectionLabel(
            text = state.selectedDay.format(formatter).uppercase(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )

        if (state.classesForDay.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No classes today",
                    fontFamily = CaveatFamily,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink2,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.classesForDay) { scheduled ->
                    TimelineClassItem(
                        scheduled = scheduled,
                        onClick = { onSubjectClick(scheduled.subject.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineClassItem(scheduled: ScheduledClass, onClick: () -> Unit) {
    val statusColor = when (scheduled.record?.status) {
        AttendanceStatus.PRESENT -> Present
        AttendanceStatus.ABSENT -> Absent
        AttendanceStatus.HOLIDAY -> Holiday
        null -> Ink3
    }
    val statusLabel = when (scheduled.record?.status) {
        AttendanceStatus.PRESENT -> "Present"
        AttendanceStatus.ABSENT -> "Absent"
        AttendanceStatus.HOLIDAY -> "Holiday"
        null -> "Unmarked"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Time column
        Text(
            text = String.format("%02d:%02d", scheduled.subject.classHour, scheduled.subject.classMinute),
            style = MaterialTheme.typography.bodySmall,
            color = Ink2,
            modifier = Modifier.width(40.dp),
        )

        // Status dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(statusColor, CircleShape),
        )

        // Subject info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scheduled.subject.name,
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = scheduled.subject.code,
                style = MaterialTheme.typography.bodySmall,
                color = Ink2,
            )
        }

        // Status badge
        Text(
            text = statusLabel,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
