package com.abrarshakhi.selfattention

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.presentation.app.AppRoot
import com.abrarshakhi.selfattention.presentation.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.theme.SelfAttentionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
            SelfAttentionTheme(themeMode = settingsState.settings.themeMode) {
                AppRoot(settingsViewModel = settingsViewModel)
            }
        }
    }
}