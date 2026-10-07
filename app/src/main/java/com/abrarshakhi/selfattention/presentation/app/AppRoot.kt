package com.abrarshakhi.selfattention.presentation.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.navigation.AppNavigation
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.BottomKey
import com.abrarshakhi.selfattention.presentation.navigation.BottomNavBar
import com.abrarshakhi.selfattention.presentation.navigation.currentRoute
import com.abrarshakhi.selfattention.presentation.navigation.rememberAppBackStack
import com.abrarshakhi.selfattention.presentation.navigation.switchTapTo

/**
 * The outer layer of the app's two-layer `Scaffold`.
 *
 * This layer owns only app-level chrome — the bottom navigation bar on top-level destinations.
 * Each screen supplies its own inner `Scaffold` with its top app bar and FAB.
 *
 * It applies no window insets itself; the padding it hands down is exactly the bottom bar, and it is
 * consumed so the inner scaffolds' `safeDrawing` insets do not count the navigation bar twice.
 */
@Composable
fun AppRoot(
    settingsViewModel: SettingsViewModel,
    startRoute: AppRoute = AppRoute.Home,
) {
    val backStack = rememberAppBackStack(startRoute)
    val selectedTab = backStack.currentRoute() as? BottomKey

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (selectedTab != null) {
                BottomNavBar(
                    selected = selectedTab,
                    onSelect = backStack::switchTapTo,
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        AppNavigation(
            backStack = backStack,
            settingsViewModel = settingsViewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
