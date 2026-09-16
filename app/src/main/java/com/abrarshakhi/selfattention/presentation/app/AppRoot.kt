package com.abrarshakhi.selfattention.presentation.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.abrarshakhi.selfattention.presentation.navigation.AppNavigation
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.BottomNavBar
import com.abrarshakhi.selfattention.presentation.navigation.currentRoute
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    settingsViewModel: SettingsViewModel
) {
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Home) }
    val current = backStack.currentRoute()
    val currentChrome = current?.chrome()
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = { currentChrome?.topBar?.invoke(backStack) },
        floatingActionButton = { currentChrome?.fab?.invoke(backStack) },
        bottomBar = {
            currentChrome?.bottomBarKey?.let {
                BottomNavBar(
                    it,
                    backStack
                )
            }
        }) { innerPadding ->
        AppNavigation(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            settingsViewModel = settingsViewModel
        )
    }
}
