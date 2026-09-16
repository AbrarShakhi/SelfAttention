package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.currentRoute() = this.lastOrNull()

fun <T> SnapshotStateList<T>.switchTapTo(destination: T) {
    this.clear()
    this.add(destination)
}

fun <T> SnapshotStateList<T>.back() {
    this.removeLastOrNull()
}

fun <T> SnapshotStateList<T>.navigateTo(destination: T) = this.add(destination)