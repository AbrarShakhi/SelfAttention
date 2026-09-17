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
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsScreen
import com.abrarshakhi.selfattention.presentation.features.timeline.TimelineScreen
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel

@Composable
fun AppNavigation(
    backStack: SnapshotStateList<AppRoute>,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Home> {
                HomeScreen(onCourseClick = {
                    backStack.navigateTo(AppRoute.CourseDetails(it))
                })
            }
            entry<AppRoute.CourseDetails> {
                CourseDetailScreen(courseId = it.courseId)
            }
            entry<AppRoute.Timeline> {
                TimelineScreen(onCourseClick = {
                    backStack.navigateTo(AppRoute.CourseDetails(it))
                })
            }
            entry<AppRoute.Settings> {
                SettingsScreen(viewModel = settingsViewModel)
            }
            entry<AppRoute.CourseEditor> {
                CourseEditorScreen(
                    courseId = it.courseId,
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