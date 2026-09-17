package com.abrarshakhi.selfattention.presentation.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.navigation.AppNavigation
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.BottomNavBar
import com.abrarshakhi.selfattention.presentation.navigation.currentRoute
import com.abrarshakhi.selfattention.presentation.navigation.rememberAppBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    settingsViewModel: SettingsViewModel,
    startRoute: AppRoute = AppRoute.Home,
) {
    val backStack = rememberAppBackStack(startRoute)
    val current = backStack.currentRoute()
    val currentChrome = current?.chrome()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(current) {
        scrollBehavior.state.contentOffset = 0f
        scrollBehavior.state.heightOffset = 0f
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { currentChrome?.topBar?.invoke(backStack, scrollBehavior) },
        floatingActionButton = { currentChrome?.fab?.invoke(backStack) },
        bottomBar = {
            currentChrome?.bottomBarKey?.let {
                BottomNavBar(it, backStack)
            }
        }) { innerPadding ->
        AppNavigation(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            settingsViewModel = settingsViewModel
        )
    }
}
