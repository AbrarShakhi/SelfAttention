package com.abrarshakhi.selfattention.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.ShapedIcon
import com.abrarshakhi.selfattention.core.model.NextClass
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import com.abrarshakhi.selfattention.core.ui.format.countdownLabel
import com.abrarshakhi.selfattention.core.ui.format.relativeDayLabel
import java.time.Duration
import java.time.LocalDateTime

private const val CountdownWindowMinutes = 60f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NextClassCard(
    nextClass: NextClass,
    now: LocalDateTime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val minutesUntil = Duration.between(now, nextClass.scheduledAt).toMinutes()
    val day = nextClass.scheduledAt.toLocalDate().relativeDayLabel(now.toLocalDate(), locale)

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShapedIcon(
                    icon = Icons.Rounded.Schedule,
                    polygon = MaterialShapes.Clover4Leaf,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    size = 52.dp,
                    spin = minutesUntil in 0..CountdownWindowMinutes.toLong(),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Up next", style = MaterialTheme.typography.labelLarge)
                    Text(text = nextClass.course.name, style = MaterialTheme.typography.titleLargeEmphasized)
                    Text(
                        text = "$day · ${nextClass.scheduledAt.clockLabel()}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            if (minutesUntil in 0..CountdownWindowMinutes.toLong()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearWavyProgressIndicator(
                        progress = { 1f - minutesUntil / CountdownWindowMinutes },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.16f),
                    )
                    Text(
                        text = countdownLabel(now, nextClass.scheduledAt),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
