package com.abrarshakhi.selfattention.core.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.connectedButtonShapes
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.forStatus
import com.abrarshakhi.selfattention.core.model.AttendanceStatus

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceToggleGroup(
    selected: AttendanceStatus?,
    onSelect: (AttendanceStatus?) -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: ToggleButtonSize = ToggleButtonSize.Small,
    showLabels: Boolean = true,
) {
    val statuses = AttendanceStatus.entries
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        statuses.forEachIndexed { index, status ->
            val palette = AppTheme.status.forStatus(status)
            val checked = status == selected
            ToggleButton(
                checked = checked,
                onCheckedChange = { onSelect(if (it) status else null) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                buttonSize = buttonSize,
                shapes = connectedButtonShapes(index, statuses.size),
                colors = ToggleButtonDefaults.colors(
                    checkedContainerColor = palette.color,
                    checkedContentColor = palette.onColor,
                ),
            ) {
                Icon(status.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                if (showLabels) {
                    Spacer(Modifier.width(6.dp))
                    Text(status.label, maxLines = 1)
                }
            }
        }
    }
}
