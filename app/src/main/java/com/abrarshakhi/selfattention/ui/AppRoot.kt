package com.abrarshakhi.selfattention.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var lastDestination by remember { mutableStateOf(selectedDestination ?: TopLevelDestination.HOME) }
    if (selectedDestination != null) lastDestination = selectedDestination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = selectedDestination != null,
                enter = expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec(), Alignment.Top),
                exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec(), Alignment.Top),
            ) {
                AppNavigationBar(
                    selected = lastDestination,
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
