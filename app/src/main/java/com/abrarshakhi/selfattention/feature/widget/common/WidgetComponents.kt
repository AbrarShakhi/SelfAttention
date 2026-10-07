package com.abrarshakhi.selfattention.feature.widget.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.abrarshakhi.selfattention.core.model.Course
import java.util.Locale

@Composable
fun WidgetContainer(
    modifier: GlanceModifier = GlanceModifier,
    padding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(28.dp)
            .padding(padding),
        content = content,
    )
}

@Composable
fun CourseBadge(
    course: Course,
    size: Dp,
    background: ColorProvider = GlanceTheme.colors.primary,
    content: ColorProvider = GlanceTheme.colors.onPrimary,
) {
    Box(
        modifier = GlanceModifier
            .size(size)
            .background(background)
            .cornerRadius(size / 2),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = course.initials(),
            style = TextStyle(color = content, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.36f).sp),
        )
    }
}

@Composable
fun WidgetText(
    text: String,
    size: TextUnit,
    color: ColorProvider = GlanceTheme.colors.onSurface,
    bold: Boolean = false,
    maxLines: Int = 1,
    modifier: GlanceModifier = GlanceModifier,
) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        ),
    )
}

@Composable
fun widgetLocale(): Locale = LocalContext.current.resources.configuration.locales[0]

private fun Course.initials(): String =
    name.split(' ', '-', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
