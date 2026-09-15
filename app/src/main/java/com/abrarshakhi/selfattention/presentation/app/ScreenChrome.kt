package com.abrarshakhi.selfattention.presentation.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.BottomKey
import com.abrarshakhi.selfattention.presentation.navigation.navigate

data class ScreenChrome(
    val title: String,
    val topBar: @Composable (backStack: SnapshotStateList<AppRoute>) -> Unit = {},
    val fab: @Composable (backStack: SnapshotStateList<AppRoute>) -> Unit = {},
    val bottomBarKey: BottomKey? = null
)

fun AppRoute.chrome(): ScreenChrome = when (this) {
    is AppRoute.Home -> ScreenChrome(
        title = "Home",
        topBar = {},
        fab = { backstack ->
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Course") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Course") },
                onClick = { backstack.navigate(AppRoute.AddCourses) },
            )
        },
        bottomBarKey = this
    )

    is AppRoute.Timeline -> ScreenChrome(
        title = "Timeline",
        topBar = {},
        fab = {},
        bottomBarKey = this
    )

    is AppRoute.Settings -> ScreenChrome(
        title = "Settings",
        topBar = {},
        fab = {},
        bottomBarKey = this
    )

    is AppRoute.AddCourses -> ScreenChrome(
        title = "AddCourses",
        topBar = {},
        fab = {},
        bottomBarKey = null
    )

    else -> throw IllegalStateException()
}