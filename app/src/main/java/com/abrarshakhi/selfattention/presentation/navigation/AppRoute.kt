package com.abrarshakhi.selfattention.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Home : AppRoute, BottomKey

    @Serializable
    data object Timeline : AppRoute, BottomKey

    @Serializable
    data object Settings : AppRoute, BottomKey

    @Serializable
    data object AddCourses : AppRoute

    @Serializable
    data class CourseDetails(val courseId: Long) : AppRoute
}



