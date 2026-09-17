package com.abrarshakhi.selfattention

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.presentation.app.AppRoot
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.theme.SelfAttentionTheme
import com.abrarshakhi.selfattention.presentation.theme.isDark
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition {
            keepSplashScreen
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            val darkTheme = settingsState.settings.themeMode.isDark()

            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT, Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT, Color.TRANSPARENT
                    ) { darkTheme },
                )
            }

            SelfAttentionTheme(
                themeMode = settingsState.settings.themeMode,
                appFont = settingsState.settings.appFont,
            ) {
                if (!settingsState.isLoading) {
                    keepSplashScreen = false
                    AppRoot(
                        settingsViewModel = settingsViewModel,
                        startRoute = if (settingsState.settings.hasCompletedOnboarding) AppRoute.Home
                        else AppRoute.Onboarding
                    )
                }
            }
        }
    }
}
