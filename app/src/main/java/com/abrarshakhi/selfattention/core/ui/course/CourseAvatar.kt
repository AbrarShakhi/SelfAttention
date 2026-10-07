package com.abrarshakhi.selfattention.core.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.model.Course

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val AvatarPolygons = listOf(
    MaterialShapes.Cookie9Sided,
    MaterialShapes.Clover4Leaf,
    MaterialShapes.Sunny,
    MaterialShapes.Pentagon,
    MaterialShapes.Gem,
    MaterialShapes.SoftBurst,
    MaterialShapes.Flower,
    MaterialShapes.Puffy,
    MaterialShapes.Cookie6Sided,
)

@Composable
private fun avatarColors(index: Int): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (index % 3) {
        0 -> scheme.primaryContainer to scheme.onPrimaryContainer
        1 -> scheme.secondaryContainer to scheme.onSecondaryContainer
        else -> scheme.tertiaryContainer to scheme.onTertiaryContainer
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CourseAvatar(
    course: Course,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    val index = Math.floorMod(course.id, AvatarPolygons.size.toLong()).toInt()
    val shape = AvatarPolygons[index].toShape()
    val (defaultContainer, defaultContent) = avatarColors(index)
    val container = if (containerColor == Color.Unspecified) defaultContainer else containerColor
    val content = if (contentColor == Color.Unspecified) defaultContent else contentColor
    Box(
        modifier = modifier
            .size(size)
            .background(container, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = course.initials(),
            color = content,
            style = if (size >= 64.dp) {
                MaterialTheme.typography.titleLargeEmphasized
            } else {
                MaterialTheme.typography.titleSmallEmphasized
            },
        )
    }
}

private fun Course.initials(): String =
    name.split(' ', '-', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
