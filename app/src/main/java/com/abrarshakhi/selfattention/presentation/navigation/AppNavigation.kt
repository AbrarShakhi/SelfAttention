package com.abrarshakhi.selfattention.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.abrarshakhi.selfattention.presentation.features.addcourse.AddCourseScreen
import com.abrarshakhi.selfattention.presentation.features.coursedetail.CourseDetailScreen
import com.abrarshakhi.selfattention.presentation.features.courseeditor.CourseEditorScreen
import com.abrarshakhi.selfattention.presentation.features.home.HomeScreen
import com.abrarshakhi.selfattention.presentation.features.onboarding.OnboardingScreen
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsScreen
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.features.timeline.TimelineScreen

/**
 * Maps every [AppRoute] to its screen. This is the only place that mutates the back stack; screens
 * receive plain callbacks and know nothing about navigation.
 */
@Composable
fun AppNavigation(
    backStack: SnapshotStateList<AppRoute>,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Onboarding> {
                OnboardingScreen(
                    settingsViewModel = settingsViewModel,
                    onFinish = {
                        settingsViewModel.completeOnboarding()
                        backStack.switchTapTo(AppRoute.Home)
                    },
                    onAddCourse = {
                        settingsViewModel.completeOnboarding()
                        backStack.switchTapTo(AppRoute.Home)
                        backStack.navigateTo(AppRoute.AddCourses)
                    },
                )
            }
            entry<AppRoute.Home> {
                HomeScreen(
                    onCourseClick = { backStack.navigateTo(AppRoute.CourseDetails(it)) },
                    onAddCourse = { backStack.navigateTo(AppRoute.AddCourses) },
                )
            }
            entry<AppRoute.CourseDetails> { route ->
                CourseDetailScreen(
                    courseId = route.courseId,
                    onNavigateUp = { backStack.back() },
                    onEdit = { backStack.navigateTo(AppRoute.CourseEditor(route.courseId)) },
                )
            }
            entry<AppRoute.Timeline> {
                TimelineScreen(onCourseClick = {
                    backStack.navigateTo(AppRoute.CourseDetails(it))
                })
            }
            entry<AppRoute.Settings> {
                SettingsScreen(viewModel = settingsViewModel)
            }
            entry<AppRoute.CourseEditor> { route ->
                CourseEditorScreen(
                    courseId = route.courseId,
                    onNavigateUp = { backStack.back() },
                    // Back to the course, which re-reads and shows the new values.
                    onSaved = { backStack.back() },
                    // Past the course too: its detail screen has nothing left to show.
                    onDeleted = { backStack.back(); backStack.back() },
                )
            }
            entry<AppRoute.AddCourses> {
                AddCourseScreen(onDone = { backStack.back() })
            }
        }
    )
}
