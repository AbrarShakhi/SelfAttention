package com.abrarshakhi.selfattention.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
) {
    HOME(HomeRoute, "Home", Icons.Default.Home),
    ADD(AddSubjectRoute, "Add", Icons.Default.Add),
    TIMELINE(TimelineRoute, "Timeline", Icons.Default.DateRange),
}

fun Any.isTabRoute() = this is HomeRoute || this is AddSubjectRoute || this is TimelineRoute
