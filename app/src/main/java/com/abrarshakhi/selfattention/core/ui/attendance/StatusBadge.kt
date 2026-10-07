package com.abrarshakhi.selfattention.core.ui.attendance

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.forStatus
import com.abrarshakhi.selfattention.core.model.AttendanceStatus

@Composable
fun StatusBadge(
    status: AttendanceStatus?,
    modifier: Modifier = Modifier,
    emptyLabel: String = "Unmarked",
) {
    val palette = status?.let { AppTheme.status.forStatus(it) }
    val container by animateColorAsState(
        palette?.colorContainer ?: MaterialTheme.colorScheme.surfaceContainerHighest,
        label = "badgeContainer",
    )
    val content by animateColorAsState(
        palette?.onColorContainer ?: MaterialTheme.colorScheme.onSurfaceVariant,
        label = "badgeContent",
    )
    Surface(modifier = modifier, shape = MaterialTheme.shapes.extraLarge, color = container, contentColor = content) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = status?.icon ?: Icons.Rounded.Schedule,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
            Text(text = status?.label ?: emptyLabel, style = MaterialTheme.typography.labelMedium)
        }
    }
}
