package com.abrarshakhi.selfattention.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.abrarshakhi.selfattention.domain.model.NextClass
import com.abrarshakhi.selfattention.presentation.components.AttendanceRing
import com.abrarshakhi.selfattention.presentation.components.SectionLabel
import com.abrarshakhi.selfattention.presentation.components.SketchBox
import com.abrarshakhi.selfattention.presentation.components.SubjectCard
import com.abrarshakhi.selfattention.ui.theme.CaveatFamily
import com.abrarshakhi.selfattention.ui.theme.Ink2
import com.abrarshakhi.selfattention.ui.theme.Today
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    onSubjectClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Today)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Self Attendance",
                    fontFamily = CaveatFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        item {
            StatsHero(
                progress = state.overallStats.attendancePercentage,
                nextClass = state.nextClass,
            )
        }

        if (state.subjects.isEmpty()) {
            item {
                EmptyState()
            }
        } else {
            item {
                SectionLabel(text = "MY SUBJECTS", modifier = Modifier.padding(top = 4.dp))
            }
            itemsIndexed(state.subjects) { index, subject ->
                SubjectCard(
                    subject = subject,
                    stats = state.statsMap[subject.id],
                    seed = index + 5,
                    onClick = { onSubjectClick(subject.id) },
                )
            }
        }
    }
}

@Composable
private fun StatsHero(
    progress: Float,
    nextClass: NextClass?,
) {
    SketchBox(seed = 1, contentPadding = 14.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AttendanceRing(progress = progress, size = 90.dp, label = "overall")
            Spacer(Modifier.width(4.dp))
            NextClassInfo(nextClass = nextClass)
        }
    }
}

@Composable
private fun NextClassInfo(nextClass: NextClass?) {
    Column {
        SectionLabel(text = "NEXT CLASS")
        Spacer(Modifier.height(4.dp))
        if (nextClass == null) {
            Text(
                text = "No upcoming\nclasses",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.headlineSmall,
                color = Ink2,
            )
        } else {
            Text(
                text = nextClass.subject.name,
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val now = LocalDateTime.now()
            val minutesUntil = ChronoUnit.MINUTES.between(now, nextClass.scheduledAt)
            val timeLabel = when {
                minutesUntil < 60 -> "in ${minutesUntil}m · ${nextClass.scheduledAt.format(formatter)}"
                nextClass.scheduledAt.toLocalDate() == now.toLocalDate() ->
                    "today · ${nextClass.scheduledAt.format(formatter)}"
                else -> "tomorrow · ${nextClass.scheduledAt.format(formatter)}"
            }
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = Today,
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
    ) {
        Text(
            text = "✏",
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No subjects yet",
            fontFamily = CaveatFamily,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Tap + to add your first subject",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink2,
        )
    }
}
