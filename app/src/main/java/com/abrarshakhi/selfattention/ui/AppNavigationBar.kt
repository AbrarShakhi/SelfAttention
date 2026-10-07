package com.abrarshakhi.selfattention.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abrarshakhi.selfattention.navigation.TopLevelDestination

@Composable
fun AppNavigationBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    ShortNavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { destination ->
            val isSelected = destination == selected
            ShortNavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
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
