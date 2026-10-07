package com.abrarshakhi.selfattention.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.AttendanceDial
import com.abrarshakhi.selfattention.core.designsystem.component.rememberSlowRotation
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.model.ATTENDANCE_TARGET
import com.abrarshakhi.selfattention.core.model.OverallStats
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverviewCard(stats: OverallStats, modifier: Modifier = Modifier) {
    val backdrop = MaterialShapes.Cookie12Sided.toShape()
    val rotation = rememberSlowRotation()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .rotate(rotation)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f), backdrop),
                )
                AttendanceDial(
                    progress = stats.attendancePercentage,
                    isAtRisk = stats.isAtRisk,
                    size = 116.dp,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Overall attendance", style = MaterialTheme.typography.titleMediumEmphasized)
                Text(
                    text = if (stats.countable == 0) {
                        "Nothing marked yet"
                    } else {
                        "${stats.totalPresent} of ${stats.countable} classes attended"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (stats.countable > 0) StandingPill(isAtRisk = stats.isAtRisk)
            }
        }
    }
}

@Composable
private fun StandingPill(isAtRisk: Boolean) {
    val palette = if (isAtRisk) AppTheme.status.absent else AppTheme.status.present
    val target = (ATTENDANCE_TARGET * 100).roundToInt()
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = palette.colorContainer,
        contentColor = palette.onColorContainer,
    ) {
        Text(
            text = if (isAtRisk) "Below $target%" else "On track",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
