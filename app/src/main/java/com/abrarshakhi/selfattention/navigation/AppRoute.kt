package com.abrarshakhi.selfattention.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Timeline : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data object AddCourse : AppRoute

    @Serializable
    data class CourseDetail(val courseId: Long) : AppRoute

    @Serializable
    data class CourseEditor(val courseId: Long) : AppRoute
}
