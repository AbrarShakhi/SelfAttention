package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.domain.model.Subject
import com.abrarshakhi.selfattention.domain.model.SubjectStats
import com.abrarshakhi.selfattention.presentation.theme.Absent
import com.abrarshakhi.selfattention.presentation.theme.CaveatFamily
import com.abrarshakhi.selfattention.presentation.theme.Present
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SubjectCard(
    subject: Subject,
    stats: SubjectStats?,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pct = stats?.attendancePercentage ?: 0f
    val pctColor = if (pct < 0.75f) Absent else Present

    SketchBox(
        seed = seed,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentPadding = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontFamily = CaveatFamily,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subject.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subject.scheduleDays.joinToString(" ") { d ->
                        d.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    } + " · " + String.format("%02d:%02d", subject.classHour, subject.classMinute),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${(pct * 100).toInt()}%",
                fontFamily = CaveatFamily,
                style = MaterialTheme.typography.headlineSmall,
                color = pctColor,
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
