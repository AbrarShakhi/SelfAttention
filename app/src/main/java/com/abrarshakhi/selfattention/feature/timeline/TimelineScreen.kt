package com.abrarshakhi.selfattention.feature.timeline

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.designsystem.component.EmptyState
import com.abrarshakhi.selfattention.core.ui.format.longLabel
import com.abrarshakhi.selfattention.feature.timeline.component.ExpandableCalendar
import com.abrarshakhi.selfattention.feature.timeline.component.TimelineClassCard
import com.abrarshakhi.selfattention.feature.timeline.component.rememberCalendarNestedScroll
import java.time.LocalDate
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimelineScreen(
    onCourseClick: (Long) -> Unit,
    viewModel: TimelineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var calendarExpanded by rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isToday = state.selectedDay == state.now.toLocalDate()

    val calendarScroll = rememberCalendarNestedScroll(
        expanded = calendarExpanded,
        onExpandedChange = { calendarExpanded = it },
        isContentAtTop = {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        },
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Timeline", style = MaterialTheme.typography.headlineSmallEmphasized) },
                actions = {
                    AnimatedVisibility(visible = !isToday, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                        FilledTonalButton(
                            onClick = viewModel::showToday,
                            shapes = ButtonDefaults.shapes(),
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Icon(Icons.Rounded.Today, contentDescription = null)
                            Text("Today", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(calendarScroll),
        ) {
            ExpandableCalendar(
                selectedDate = state.selectedDay,
                visibleMonth = state.visibleMonth,
                weekStart = state.weekStartDay,
                expanded = calendarExpanded,
                classCountOn = state::classCountOn,
                onDateSelected = viewModel::selectDay,
                onPreviousMonth = viewModel::showPreviousMonth,
                onNextMonth = viewModel::showNextMonth,
                onToggleExpanded = { calendarExpanded = !calendarExpanded },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
            DayHeader(date = state.schedule.date, classCount = state.schedule.classes.size)
            AnimatedContent(
                targetState = state.schedule,
                modifier = Modifier.weight(1f),
                contentKey = { it.date },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "timelineDay",
            ) { (day, classes) ->
                if (classes.isEmpty()) {
                    EmptyDay(day = day, isHoliday = day.dayOfWeek in state.weeklyHolidays, isToday = day == state.now.toLocalDate())
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(classes, key = { it.course.id }) { scheduled ->
                            TimelineClassCard(
                                scheduled = scheduled,
                                now = state.now,
                                onMark = { viewModel.mark(scheduled, it) },
                                onClick = { onCourseClick(scheduled.course.id) },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DayHeader(date: LocalDate, classCount: Int) {
    val locale = LocalLocale.current.platformLocale
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = date.longLabel(locale),
            style = MaterialTheme.typography.titleMediumEmphasized,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = when (classCount) {
                0 -> "No classes"
                1 -> "1 class"
                else -> "$classCount classes"
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyDay(day: LocalDate, isHoliday: Boolean, isToday: Boolean) {
    val locale = LocalLocale.current.platformLocale
    EmptyState(
        animation = R.raw.empty_day,
        title = when {
            isHoliday -> "Weekly holiday"
            isToday -> "Nothing scheduled today"
            else -> "Nothing scheduled"
        },
        message = when {
            isHoliday -> "Classes on this day are never counted. Change it in Settings."
            isToday -> "Enjoy the free time."
            else -> "No classes meet on ${day.dayOfWeek.getDisplayName(TextStyle.FULL, locale)}."
        },
        illustrationSize = 160.dp,
    )
}
