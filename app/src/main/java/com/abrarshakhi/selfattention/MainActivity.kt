package com.abrarshakhi.selfattention

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abrarshakhi.selfattention.navigation.AppNavigation
import com.abrarshakhi.selfattention.presentation.settings.SettingsViewModel
import com.abrarshakhi.selfattention.ui.theme.SelfAttentionTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.state.collectAsState()
            SelfAttentionTheme(themeMode = settingsState.settings.themeMode) {
                AppNavigation()
            }
        }
    }
}
