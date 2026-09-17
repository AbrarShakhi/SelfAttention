package com.abrarshakhi.selfattention.presentation.features.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.domain.model.OverallStats
import com.abrarshakhi.selfattention.presentation.components.AttendanceRing
import com.abrarshakhi.selfattention.presentation.components.SectionLabel
import com.abrarshakhi.selfattention.presentation.components.CourseCard
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private const val AtRiskThreshold = 0.75f

@Composable
fun HomeScreen(
    onCourseClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Surface
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "overall") {
                OverallCard(stats = state.overallStats)
            }

            state.nextClass?.let { next ->
                item(key = "next") {
                    NextClassCard(nextClass = next)
                }
            }

            if (state.courses.isEmpty()) {
                item(key = "empty") { EmptyState() }
            } else {
                item(key = "courses-header") {
                    SectionLabel(
                        text = "My courses",
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    )
                }
                items(state.courses, key = { it.id }) { course ->
                    CourseCard(
                        course = course,
                        stats = state.statsMap[course.id],
                        onClick = { onCourseClick(course.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallCard(stats: OverallStats) {
    val countable = stats.totalPresent + stats.totalAbsent
    val atRisk = countable > 0 && stats.attendancePercentage < AtRiskThreshold

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AttendanceRing(progress = stats.attendancePercentage, size = 104.dp)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Overall attendance",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (countable == 0) {
                        "Nothing marked yet"
                    } else {
                        "${stats.totalPresent} of $countable classes attended"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (atRisk) {
                    Text(
                        text = "Below 75%",
                        style = MaterialTheme.typography.labelLarge,
                        color = AppTheme.status.absent.color,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun NextClassCard(nextClass: NextClass) {
    val locale = LocalLocale.current.platformLocale
    val timeLabel = remember(nextClass, locale) { nextClassLabel(nextClass, locale) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Next class", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = nextClass.course.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = timeLabel, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Human-readable time until the next class.
 *
 * Anything that is not today is checked against tomorrow explicitly — the previous version fell
 * through to "tomorrow" for every future date, so a class three days out also read as tomorrow.
 */
private fun nextClassLabel(nextClass: NextClass, locale: Locale): String {
    val now = LocalDateTime.now()
    val at = nextClass.scheduledAt
    val time = at.format(DateTimeFormatter.ofPattern("HH:mm"))
    val minutesUntil = ChronoUnit.MINUTES.between(now, at)
    val today = now.toLocalDate()
    val date = at.toLocalDate()

    return when {
        minutesUntil in 0..59 -> "In $minutesUntil min  ·  $time"
        date == today -> "Today  ·  $time"
        date == today.plusDays(1) -> "Tomorrow  ·  $time"
        else -> "${date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)}  ·  $time"
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
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
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No courses yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Add a course to start tracking your attendance.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
