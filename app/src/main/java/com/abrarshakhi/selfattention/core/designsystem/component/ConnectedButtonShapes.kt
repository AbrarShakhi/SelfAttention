package com.abrarshakhi.selfattention.core.designsystem.component

import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun connectedButtonShapes(index: Int, count: Int): ToggleButtonShapes = when (index) {
    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
    count - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
}
