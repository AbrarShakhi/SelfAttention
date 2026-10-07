package com.abrarshakhi.selfattention

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.designsystem.theme.SelfAttentionTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.isDark
import com.abrarshakhi.selfattention.navigation.AppRoute
import com.abrarshakhi.selfattention.ui.AppRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.uiState.value is MainActivityUiState.Loading
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val settings = (uiState as? MainActivityUiState.Success)?.settings ?: return@setContent
            val darkTheme = settings.themeMode.isDark()

            SideEffect {
                val barStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
            }

            SelfAttentionTheme(
                themeMode = settings.themeMode,
                appFont = settings.appFont,
                colors = settings.colorPreferences,
            ) {
                AppRoot(
                    startRoute = if (settings.hasCompletedOnboarding) AppRoute.Home else AppRoute.Onboarding,
                )
            }
        }
    }
}
