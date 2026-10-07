package com.abrarshakhi.selfattention.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.abrarshakhi.selfattention.navigation.TopLevelDestination

@Composable
fun AppNavigationBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == selected,
                onClick = { onSelect(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(destination.label) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavigationBarPreview() {
    AppNavigationBar(selected = TopLevelDestination.TIMELINE, onSelect = {})
}
