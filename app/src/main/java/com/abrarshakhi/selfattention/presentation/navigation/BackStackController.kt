package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.serialization.json.Json

fun <T> SnapshotStateList<T>.currentRoute() = this.lastOrNull()

fun <T> SnapshotStateList<T>.switchTapTo(destination: T) {
    this.clear()
    this.add(destination)
}

fun <T> SnapshotStateList<T>.back() {
    this.removeLastOrNull()
}

fun <T> SnapshotStateList<T>.navigateTo(destination: T) = this.add(destination)


val AppRouteBackStackSaver: Saver<SnapshotStateList<AppRoute>, Any> =
    listSaver(
        save = { stack -> stack.map { Json.encodeToString<AppRoute>(it) } },
        restore = { saved ->
            val routes = saved.mapNotNull { encoded ->
                runCatching { Json.decodeFromString<AppRoute>(encoded) }.getOrNull()
            }
            mutableStateListOf<AppRoute>().apply {
                addAll(routes.ifEmpty { listOf(AppRoute.Home) })
            }
        },
    )

@Composable
fun rememberAppBackStack(): SnapshotStateList<AppRoute> =
    rememberSaveable(saver = AppRouteBackStackSaver) {
        mutableStateListOf(AppRoute.Home)
    }
