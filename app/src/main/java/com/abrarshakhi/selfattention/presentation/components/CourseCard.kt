package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.domain.model.Course
import com.abrarshakhi.selfattention.domain.model.CourseStats
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import kotlin.math.roundToInt

private const val AtRiskThreshold = 0.75f

@Composable
fun CourseCard(
    course: Course,
    stats: CourseStats?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val pct = (stats?.attendancePercentage ?: 0f).coerceIn(0f, 1f)
    val atRisk = pct < AtRiskThreshold
    val accent = if (atRisk) AppTheme.status.absent.color else AppTheme.status.present.color

    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(pct) { target = pct }
    val animatedPct by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "courseProgress",
    )

    val meta = remember(course, locale) {
        val days = course.scheduleDays.sorted()
            .joinToString(" ") { it.getDisplayName(TextStyle.SHORT, locale) }
        val time = course.classTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        listOf(course.code, days, time).filter { it.isNotBlank() }.joinToString("  ·  ")
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${(animatedPct * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            LinearProgressIndicator(
                progress = { animatedPct },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }
    }
}
