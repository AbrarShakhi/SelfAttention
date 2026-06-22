package com.abrarshakhi.selfattention.navigation

import kotlinx.serialization.Serializable

@Serializable data object HomeRoute
@Serializable data object AddSubjectRoute
@Serializable data object TimelineRoute
@Serializable data object SettingsRoute
@Serializable data class SubjectDetailRoute(val subjectId: Long)
