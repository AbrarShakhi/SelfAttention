package com.abrarshakhi.selfattention.feature.timeline.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.ui.calendar.leadingBlankCount
import com.abrarshakhi.selfattention.core.ui.calendar.startOfWeek
import com.abrarshakhi.selfattention.core.ui.calendar.weekdayOrder
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalIconButton(onClick = onPreviousMonth, shapes = IconButtonDefaults.shapes()) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Previous month")
            }
            AnimatedContent(
                targetState = visibleMonth,
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally { direction * it / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -direction * it / 3 } + fadeOut())
                },
                label = "timelineMonth",
            ) { month ->
                Text(
                    text = "${month.month.getDisplayName(TextStyle.FULL, locale)} ${month.year}",
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            FilledTonalIconButton(onClick = onNextMonth, shapes = IconButtonDefaults.shapes()) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Next month")
            }
        }
        Spacer(Modifier.height(8.dp))
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    val selection by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "daySelection",
    )
    val selectedShape = MaterialShapes.Cookie9Sided.toShape()
    val content = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        isOutsideMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val dotColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selection > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(0.6f + 0.4f * selection)
                    .rotate(45f * selection)
                    .background(MaterialTheme.colorScheme.primary, selectedShape),
            )
        }
        if (isToday && !isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = content,
            )
            if (classCount > 0) {
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clearAndSetSemantics { },
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    repeat(classCount.coerceAtMost(3)) {
                        Box(Modifier.size(4.dp).background(dotColor, CircleShape))
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
