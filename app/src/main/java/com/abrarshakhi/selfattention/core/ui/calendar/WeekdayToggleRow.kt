package com.abrarshakhi.selfattention.core.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.toggleColors
import java.time.DayOfWeek
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeekdayToggleRow(
    selected: Set<DayOfWeek>,
    onToggle: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    checkedContainerColor: Color = Color.Unspecified,
    checkedContentColor: Color = Color.Unspecified,
) {
    val locale = LocalLocale.current.platformLocale
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        weekdayOrder(weekStart).forEach { day ->
            val fullName = day.getDisplayName(TextStyle.FULL, locale)
            ToggleButton(
                checked = day in selected,
                onCheckedChange = { onToggle(day) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .semantics { contentDescription = fullName },
                colors = if (checkedContainerColor == Color.Unspecified) {
                    toggleColors()
                } else {
                    toggleColors(checkedContainerColor, checkedContentColor)
                },
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(text = day.getDisplayName(TextStyle.NARROW, locale), maxLines = 1)
            }
        }
    }
}
