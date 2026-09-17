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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun BottomNavBar(selectedKey: BottomKey, backStack: SnapshotStateList<AppRoute>) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedKey is AppRoute.Home,
            onClick = { backStack.switchTapTo(AppRoute.Home) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
        )

        NavigationBarItem(
            selected = selectedKey is AppRoute.Timeline,
            onClick = { backStack.switchTapTo(AppRoute.Timeline) },
            icon = { Icon(imageVector = Icons.Default.Timeline, contentDescription = "Timeline") },
            label = { Text("Timeline") },
        )

        NavigationBarItem(
            selected = selectedKey is AppRoute.Settings,
            onClick = { backStack.switchTapTo(AppRoute.Settings) },
            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    BottomNavBar(selectedKey = AppRoute.Timeline, backStack = SnapshotStateList())
}