package com.abrarshakhi.selfattention.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: AppRoute,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
) {
    HOME(AppRoute.Home, Icons.Rounded.Home, Icons.Outlined.Home, "Home"),
    TIMELINE(AppRoute.Timeline, Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth, "Timeline"),
    SETTINGS(AppRoute.Settings, Icons.Rounded.Settings, Icons.Outlined.Settings, "Settings"),
    ;

    companion object {
        fun of(route: AppRoute?): TopLevelDestination? = entries.firstOrNull { it.route == route }
    }
}
