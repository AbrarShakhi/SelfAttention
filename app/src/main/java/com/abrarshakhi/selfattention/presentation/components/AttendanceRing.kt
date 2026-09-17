package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import kotlin.math.roundToInt

private const val AtRiskThreshold = 0.75f

@Composable
fun AttendanceRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    strokeWidth: Dp = 10.dp,
    label: String? = null,
) {
    // Start at zero so the first frame animates in; targetValue == initial would render statically.
    var target by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { target = progress.coerceIn(0f, 1f) }

    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "attendanceSweep",
    )
    val ringColor by animateColorAsState(
        targetValue = if (target < AtRiskThreshold) {
            AppTheme.status.absent.color
        } else {
            AppTheme.status.present.color
        },
        animationSpec = tween(durationMillis = 600),
        label = "attendanceColor",
    )

    val percent = (animatedProgress * 100).roundToInt()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(size),
            color = ringColor,
            strokeWidth = strokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = StrokeCap.Round,
        )
        // The indicator already reports progress to accessibility services; don't say it twice.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clearAndSetSemantics { },
        ) {
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
