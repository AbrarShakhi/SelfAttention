package com.abrarshakhi.selfattention.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.ui.theme.Ink

// Slightly irregular corner radii for hand-drawn feel
fun sketchShape(seed: Int): RoundedCornerShape {
    fun jitter(i: Int): Dp {
        val x = kotlin.math.sin((seed + i) * 9999.0) * 10000
        val frac = x - kotlin.math.floor(x)
        return (8 + (frac - 0.5) * 4).dp
    }
    return RoundedCornerShape(
        topStart = jitter(0),
        topEnd = jitter(1),
        bottomEnd = jitter(2),
        bottomStart = jitter(3),
    )
}

@Composable
fun SketchBox(
    modifier: Modifier = Modifier,
    seed: Int = 1,
    strokeWidth: Dp = 1.5.dp,
    strokeColor: Color = Ink,
    contentPadding: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .border(strokeWidth, strokeColor, sketchShape(seed))
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}
