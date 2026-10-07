package com.abrarshakhi.selfattention.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.abrarshakhi.selfattention.feature.course.add.AddCourseScreen
import com.abrarshakhi.selfattention.feature.course.detail.CourseDetailScreen
import com.abrarshakhi.selfattention.feature.course.edit.CourseEditorScreen
import com.abrarshakhi.selfattention.feature.home.HomeScreen
import com.abrarshakhi.selfattention.feature.onboarding.OnboardingScreen
import com.abrarshakhi.selfattention.feature.settings.SettingsScreen
import com.abrarshakhi.selfattention.feature.timeline.TimelineScreen

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<AppRoute>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Onboarding> {
                OnboardingScreen(
                    onFinish = { backStack.switchTapTo(AppRoute.Home) },
                    onAddCourse = {
                        backStack.switchTapTo(AppRoute.Home)
                        backStack.navigateTo(AppRoute.AddCourse)
                    },
                )
            }
            entry<AppRoute.Home> {
                HomeScreen(
                    onCourseClick = { backStack.navigateTo(AppRoute.CourseDetail(it)) },
                    onAddCourse = { backStack.navigateTo(AppRoute.AddCourse) },
                )
            }
            entry<AppRoute.Timeline> {
                TimelineScreen(
                    onCourseClick = { backStack.navigateTo(AppRoute.CourseDetail(it)) },
                )
            }
            entry<AppRoute.Settings> {
                SettingsScreen()
            }
            entry<AppRoute.AddCourse> {
                AddCourseScreen(onDone = { backStack.back() })
            }
            entry<AppRoute.CourseDetail> { route ->
                CourseDetailScreen(
                    courseId = route.courseId,
                    onNavigateUp = { backStack.back() },
                    onEdit = { backStack.navigateTo(AppRoute.CourseEditor(route.courseId)) },
                )
            }
            entry<AppRoute.CourseEditor> { route ->
                CourseEditorScreen(
                    courseId = route.courseId,
                    onNavigateUp = { backStack.back() },
                    onSaved = { backStack.back() },
                    onDeleted = {
                        backStack.back()
                        backStack.back()
                    },
                )
            }
        },
    )
}
