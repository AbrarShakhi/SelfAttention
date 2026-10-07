package com.abrarshakhi.selfattention

import com.abrarshakhi.selfattention.core.model.AppSettings

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val settings: AppSettings) : MainActivityUiState
}
