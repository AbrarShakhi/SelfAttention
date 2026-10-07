package com.abrarshakhi.selfattention.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: AppRoute,
    val icon: ImageVector,
    val label: String,
) {
    HOME(AppRoute.Home, Icons.Default.Home, "Home"),
    TIMELINE(AppRoute.Timeline, Icons.Default.Timeline, "Timeline"),
    SETTINGS(AppRoute.Settings, Icons.Default.Settings, "Settings"),
    ;

    companion object {
        fun of(route: AppRoute?): TopLevelDestination? = entries.firstOrNull { it.route == route }
    }
}
