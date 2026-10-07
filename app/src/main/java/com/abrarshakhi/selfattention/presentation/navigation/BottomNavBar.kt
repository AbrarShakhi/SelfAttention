package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

private data class BottomDestination(
    val route: AppRoute,
    val icon: ImageVector,
    val label: String,
)

private val BottomDestinations = listOf(
    BottomDestination(AppRoute.Home, Icons.Default.Home, "Home"),
    BottomDestination(AppRoute.Timeline, Icons.Default.Timeline, "Timeline"),
    BottomDestination(AppRoute.Settings, Icons.Default.Settings, "Settings"),
)

/**
 * Navigation bar for the top-level destinations.
 *
 * @param onSelect called with the tapped destination; the caller decides how the back stack changes.
 */
@Composable
fun BottomNavBar(
    selected: BottomKey,
    onSelect: (AppRoute) -> Unit,
) {
    NavigationBar {
        BottomDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.route == selected,
                onClick = { onSelect(destination.route) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(destination.label) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    BottomNavBar(selected = AppRoute.Timeline, onSelect = {})
}
