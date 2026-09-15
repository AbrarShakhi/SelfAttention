package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.currentRoute() = this.lastOrNull()

fun <T> SnapshotStateList<T>.navigateTapTo(destination: T) {
    this.clear()
    this.add(destination)
}

fun <T> SnapshotStateList<T>.back() {
    this.removeLastOrNull()
}

fun <T> SnapshotStateList<T>.navigate(destination: T) = this.add(destination)