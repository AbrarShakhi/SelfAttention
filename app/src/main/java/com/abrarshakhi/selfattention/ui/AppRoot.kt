package com.abrarshakhi.selfattention.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abrarshakhi.selfattention.navigation.AppNavigation
import com.abrarshakhi.selfattention.navigation.AppRoute
import com.abrarshakhi.selfattention.navigation.TopLevelDestination
import com.abrarshakhi.selfattention.navigation.currentRoute
import com.abrarshakhi.selfattention.navigation.rememberAppBackStack
import com.abrarshakhi.selfattention.navigation.switchTapTo

@Composable
fun AppRoot(startRoute: AppRoute) {
    val backStack = rememberAppBackStack(startRoute)
    val selectedDestination = TopLevelDestination.of(backStack.currentRoute())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (selectedDestination != null) {
                AppNavigationBar(
                    selected = selectedDestination,
                    onSelect = { backStack.switchTapTo(it.route) },
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        AppNavigation(
            backStack = backStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
