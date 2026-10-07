package com.abrarshakhi.selfattention.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigateUpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, shapes = IconButtonDefaults.shapes()) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailTopAppBar(
    title: String,
    onNavigateUp: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    MediumFlexibleTopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        subtitle = subtitle?.let { { Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis) } },
        navigationIcon = { NavigateUpButton(onClick = onNavigateUp) },
        actions = actions,
        scrollBehavior = scrollBehavior,
    )
}
