package com.abrarshakhi.selfattention.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceDial(
    progress: Float,
    isAtRisk: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
    strokeWidth: Dp = 10.dp,
    label: String? = null,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        label = "dialProgress",
    )
    val indicatorColor by animateColorAsState(
        targetValue = when {
            progress <= 0f -> MaterialTheme.colorScheme.primary
            isAtRisk -> AppTheme.status.absent.color
            else -> AppTheme.status.present.color
        },
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        label = "dialColor",
    )
    val density = LocalDensity.current
    val stroke = remember(strokeWidth, density) {
        Stroke(width = with(density) { strokeWidth.toPx() }, cap = StrokeCap.Round)
    }
    val percent = (animatedProgress * 100).roundToInt()

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "$percent percent attendance" },
        contentAlignment = Alignment.Center,
    ) {
        CircularWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = indicatorColor,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            stroke = stroke,
            trackStroke = stroke,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percent%",
                style = if (size >= 112.dp) {
                    MaterialTheme.typography.headlineMediumEmphasized
                } else {
                    MaterialTheme.typography.titleMediumEmphasized
                },
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
