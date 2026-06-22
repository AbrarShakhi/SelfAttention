package com.abrarshakhi.selfattention.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.abrarshakhi.selfattention.presentation.addsubject.AddSubjectScreen
import com.abrarshakhi.selfattention.presentation.home.HomeScreen
import com.abrarshakhi.selfattention.presentation.settings.SettingsScreen
import com.abrarshakhi.selfattention.presentation.subjectdetail.SubjectDetailScreen
import com.abrarshakhi.selfattention.presentation.timeline.TimelineScreen
import com.abrarshakhi.selfattention.ui.theme.Ink
import com.abrarshakhi.selfattention.ui.theme.Ink2
import com.abrarshakhi.selfattention.ui.theme.Paper
import com.abrarshakhi.selfattention.ui.theme.Today

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }
    val currentRoute by remember { derivedStateOf { backStack.lastOrNull() } }
    val showBottomNav by remember { derivedStateOf { currentRoute?.isTabRoute() == true } }

    fun switchTab(route: Any) {
        while (backStack.size > 1) backStack.removeLastOrNull()
        if (backStack.firstOrNull() != route) {
            if (backStack.isNotEmpty()) backStack[0] = route
            else backStack.add(route)
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(containerColor = Paper, contentColor = Ink) {
                    BottomNavItem.entries.forEach { item ->
                        val selected = currentRoute?.let { it::class == item.route::class } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { switchTab(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Today,
                                selectedTextColor = Today,
                                unselectedIconColor = Ink2,
                                unselectedTextColor = Ink2,
                                indicatorColor = Paper,
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    HomeScreen(
                        onSubjectClick = { id -> backStack.add(SubjectDetailRoute(id)) },
                        onSettingsClick = { backStack.add(SettingsRoute) },
                    )
                }
                entry<AddSubjectRoute> {
                    AddSubjectScreen(onDone = { switchTab(HomeRoute) })
                }
                entry<TimelineRoute> {
                    TimelineScreen(
                        onSubjectClick = { id -> backStack.add(SubjectDetailRoute(id)) },
                    )
                }
                entry<SettingsRoute> {
                    SettingsScreen(onBack = { backStack.removeLastOrNull() })
                }
                entry<SubjectDetailRoute> { key ->
                    SubjectDetailScreen(
                        subjectId = key.subjectId,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
        )
    }
}
