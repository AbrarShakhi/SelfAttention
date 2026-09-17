package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@Composable
fun ExpandableCalendar(
    selectedDate: LocalDate,
    visibleMonth: YearMonth,
    weekStart: DayOfWeek,
    expanded: Boolean,
    classCountOn: (LocalDate) -> Int,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val today = remember { LocalDate.now() }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                )
            }
            Text(
                text = "${visibleMonth.month.getDisplayName(TextStyle.FULL, locale)} " +
                    "${visibleMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                )
            }
        }

        // Weekday labels stay put across both modes so the columns never shift.
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayOrder(weekStart).forEach { dow ->
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

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(250), expandFrom = Alignment.Top) + fadeIn(tween(250)),
            exit = shrinkVertically(tween(250), shrinkTowards = Alignment.Top) + fadeOut(tween(150)),
        ) {
            MonthGrid(
                month = visibleMonth,
                weekStart = weekStart,
                selectedDate = selectedDate,
                today = today,
                classCountOn = classCountOn,
                onDateSelected = onDateSelected,
            )
        }

        AnimatedVisibility(
            visible = !expanded,
            enter = expandVertically(tween(250), expandFrom = Alignment.Top) + fadeIn(tween(250)),
            exit = shrinkVertically(tween(250), shrinkTowards = Alignment.Top) + fadeOut(tween(150)),
        ) {
            WeekRow(
                weekStart = weekStart,
                selectedDate = selectedDate,
                today = today,
                classCountOn = classCountOn,
                onDateSelected = onDateSelected,
            )
        }

        ExpandHandle(expanded = expanded, onClick = onToggleExpanded)
    }
}

@Composable
private fun WeekRow(
    weekStart: DayOfWeek,
    selectedDate: LocalDate,
    today: LocalDate,
    classCountOn: (LocalDate) -> Int,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstDay = remember(selectedDate, weekStart) { selectedDate.startOfWeek(weekStart) }
    Row(modifier = Modifier.fillMaxWidth()) {
        repeat(7) { index ->
            val date = firstDay.plusDays(index.toLong())
            DayCell(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == today,
                isOutsideMonth = false,
                classCount = classCountOn(date),
                onClick = { onDateSelected(date) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    weekStart: DayOfWeek,
    selectedDate: LocalDate,
    today: LocalDate,
    classCountOn: (LocalDate) -> Int,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstOfMonth = month.atDay(1)
    val leadingBlanks = leadingBlankCount(firstOfMonth, weekStart)
    val gridStart = firstOfMonth.minusDays(leadingBlanks.toLong())
    val rows = (leadingBlanks + month.lengthOfMonth() + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val date = gridStart.plusDays((row * 7 + col).toLong())
                    DayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        isOutsideMonth = YearMonth.from(date) != month,
                        classCount = classCountOn(date),
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    isOutsideMonth: Boolean,
    classCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    val content = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        isOutsideMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .padding(2.dp),
            shape = CircleShape,
            color = container,
            contentColor = content,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                )
                // Marks days that have classes scheduled.
                if (classCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clearAndSetSemantics { },
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            content = {},
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandHandle(expanded: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250),
        label = "calendarHandleRotation",
    )
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.height(28.dp)) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse calendar" else "Expand calendar",
                modifier = Modifier.rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


@Composable
fun rememberCalendarNestedScroll(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isContentAtTop: () -> Boolean,
    threshold: androidx.compose.ui.unit.Dp = 48.dp,
): NestedScrollConnection {
    val currentExpanded by rememberUpdatedState(expanded)
    val currentOnChange by rememberUpdatedState(onExpandedChange)
    val currentAtTop by rememberUpdatedState(isContentAtTop)
    val thresholdPx = with(LocalDensity.current) { threshold.toPx() }

    return remember {
        object : NestedScrollConnection {
            private var accumulated = 0f

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Only react to real drags; a fling should not toggle the calendar.
                if (source != NestedScrollSource.UserInput) return Offset.Zero
                val delta = available.y
                return when {
                    delta < 0f && currentExpanded -> {
                        accumulated = if (accumulated > 0f) delta else accumulated + delta
                        if (accumulated <= -thresholdPx) {
                            currentOnChange(false)
                            accumulated = 0f
                        }
                        Offset(0f, delta)
                    }

                    delta > 0f && !currentExpanded && currentAtTop() -> {
                        accumulated = if (accumulated < 0f) delta else accumulated + delta
                        if (accumulated >= thresholdPx) {
                            currentOnChange(true)
                            accumulated = 0f
                        }
                        Offset.Zero
                    }

                    else -> {
                        accumulated = 0f
                        Offset.Zero
                    }
                }
            }
        }
    }
}
