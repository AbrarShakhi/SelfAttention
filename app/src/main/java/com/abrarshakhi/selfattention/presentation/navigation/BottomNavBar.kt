package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color


@Composable
fun BottomNavBar(selectedKey: BottomKey, backStack: SnapshotStateList<AppRoute>) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedKey is AppRoute.Home,
            onClick = { backStack.navigateTapTo(AppRoute.Home) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = {
                Text(
                    "Home",
                    color = if (selectedKey is AppRoute.Home) Color.Black else Color.Gray
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.surface,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = selectedKey is AppRoute.Timeline,
            onClick = { backStack.navigateTapTo(AppRoute.Timeline) },
            icon = { Icon(imageVector = Icons.Default.Timeline, contentDescription = "Timeline") },
            label = {
                Text(
                    "Timeline",
                    color = if (selectedKey is AppRoute.Timeline) Color.Black else Color.Gray
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.surface,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = selectedKey is AppRoute.Settings,
            onClick = { backStack.navigateTapTo(AppRoute.Settings) },
            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
            label = {
                Text(
                    "Settings",
                    color = if (selectedKey is AppRoute.Settings) Color.Black else Color.Gray
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.surface,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}