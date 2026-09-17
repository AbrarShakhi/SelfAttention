package com.abrarshakhi.selfattention.presentation.features.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.presentation.components.ExpandableCalendar
import com.abrarshakhi.selfattention.presentation.components.rememberCalendarNestedScroll
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import com.abrarshakhi.selfattention.presentation.theme.StatusColor
import com.abrarshakhi.selfattention.presentation.theme.forStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JavaTextStyle

@Composable
fun TimelineScreen(
    onCourseClick: (Long) -> Unit,
    viewModel: TimelineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var calendarExpanded by rememberSaveable { mutableStateOf(true) }

    val nestedScroll = rememberCalendarNestedScroll(
        expanded = calendarExpanded,
        onExpandedChange = { calendarExpanded = it },
        isContentAtTop = {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        },
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.nestedScroll(nestedScroll)) {
            ExpandableCalendar(
                selectedDate = state.selectedDay,
                visibleMonth = state.visibleMonth,
                expanded = calendarExpanded,
                classCountOn = state::classCountOn,
                onDateSelected = viewModel::selectDay,
                onPreviousMonth = viewModel::showPreviousMonth,
                onNextMonth = viewModel::showNextMonth,
                onToggleExpanded = { calendarExpanded = !calendarExpanded },
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            HorizontalDivider()

            DayHeader(
                date = state.selectedDay,
                classCount = state.classesForDay.size,
                onTodayClick = viewModel::showToday,
            )

            if (state.classesForDay.isEmpty()) {
                EmptyDay(date = state.selectedDay, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.classesForDay, key = { it.subject.id }) { scheduled ->
                        TimelineClassCard(
                            scheduled = scheduled,
                            onClick = { onCourseClick(scheduled.subject.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(
    date: LocalDate,
    classCount: Int,
    onTodayClick: () -> Unit,
) {
    val locale = LocalLocale.current.platformLocale
    val formatted = remember(date, locale) {
        date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
    }
    val isToday = date == remember { LocalDate.now() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = formatted, style = MaterialTheme.typography.titleMedium)
            Text(
                text = when (classCount) {
                    0 -> "No classes"
                    1 -> "1 class"
                    else -> "$classCount classes"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!isToday) {
            TextButton(onClick = onTodayClick) { Text("Today") }
        }
    }
}


@Composable
private fun EmptyDay(date: LocalDate, modifier: Modifier = Modifier) {
    val locale = LocalLocale.current.platformLocale
    val isToday = date == remember { LocalDate.now() }
    val dayName = remember(date, locale) {
        date.dayOfWeek.getDisplayName(JavaTextStyle.FULL, locale)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (isToday) "Nothing scheduled today" else "Nothing scheduled",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (isToday) {
                "Enjoy the day off."
            } else {
                "You have no classes on $dayName."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TimelineClassCard(scheduled: ScheduledClass, onClick: () -> Unit) {
    val subject = scheduled.subject
    val status = scheduled.record?.status
    val family = status?.let { AppTheme.status.forStatus(it) }
    val accent = family?.color ?: MaterialTheme.colorScheme.outline

    val start = subject.classTime
    val end = remember(subject) { start.plusMinutes(subject.classDurationMinutes.toLong()) }
    val timeFormat = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.width(52.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = start.format(timeFormat),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = end.format(timeFormat),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Status stripe — colour alone carries the state here, so the badge below repeats it
            // in words for anyone who cannot rely on colour.
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp),
                shape = CircleShape,
                color = accent,
                content = {},
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = subject.name, style = MaterialTheme.typography.titleMedium)
                if (subject.code.isNotBlank()) {
                    Text(
                        text = subject.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            StatusBadge(status = status, family = family)
        }
    }
}

@Composable
private fun StatusBadge(status: AttendanceStatus?, family: StatusColor?) {
    val label = when (status) {
        AttendanceStatus.PRESENT -> "Present"
        AttendanceStatus.ABSENT -> "Absent"
        AttendanceStatus.HOLIDAY -> "Holiday"
        null -> "Unmarked"
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = family?.colorContainer ?: MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = family?.onColorContainer ?: MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
