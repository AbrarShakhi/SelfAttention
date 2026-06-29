package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.ui.theme.Ink
import com.abrarshakhi.selfattention.ui.theme.Ink2
import com.abrarshakhi.selfattention.ui.theme.Paper
import com.abrarshakhi.selfattention.ui.theme.Today
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DayStrip(
    days: List<LocalDate>,
    selectedDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        days.forEach { date ->
            val isToday = date == LocalDate.now()
            val isSelected = date == selectedDay
            DayCell(
                date = date,
                isToday = isToday,
                isSelected = isSelected,
                onClick = { onDaySelected(date) },
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = when {
        isSelected -> Today
        isToday -> Today.copy(alpha = 0.15f)
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Paper
        else -> if (isToday) Today else Ink
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Today else Ink2,
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .background(bgColor, CircleShape)
                .then(
                    if (isToday && !isSelected) Modifier.border(1.5.dp, Today, CircleShape)
                    else Modifier
                ),
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
            )
        }
    }
}
