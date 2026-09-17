package com.abrarshakhi.selfattention.presentation.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.presentation.navigation.AppRoute
import com.abrarshakhi.selfattention.presentation.navigation.BottomKey
import com.abrarshakhi.selfattention.presentation.navigation.back
import com.abrarshakhi.selfattention.presentation.navigation.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
data class ScreenChrome(
    val title: String,
    val topBar: @Composable (
        backStack: SnapshotStateList<AppRoute>,
        scrollBehavior: TopAppBarScrollBehavior,
    ) -> Unit = { _, _ -> },
    val fab: @Composable (backStack: SnapshotStateList<AppRoute>) -> Unit = {},
    val bottomBarKey: BottomKey? = null
)


@OptIn(ExperimentalMaterial3Api::class)
fun AppRoute.chrome(): ScreenChrome = when (this) {
    is AppRoute.Onboarding -> ScreenChrome(title = "Welcome")

    is AppRoute.Home -> ScreenChrome(
        title = "Home",
        topBar = { _, scrollBehavior ->
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        fab = { backstack ->
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Course") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Course") },
                onClick = { backstack.navigateTo(AppRoute.AddCourses) },
            )
        },
        bottomBarKey = this
    )

    is AppRoute.Timeline -> ScreenChrome(
        title = "Timeline",
        topBar = { _, scrollBehavior ->
            TopAppBar(
                title = { Text(text = "Timeline") },
                scrollBehavior = scrollBehavior,
            )
        },
        fab = {},
        bottomBarKey = this
    )

    is AppRoute.Settings -> ScreenChrome(
        title = "Settings",
        topBar = { _, scrollBehavior ->
            TopAppBar(
                title = { Text(text = "Settings") },
                scrollBehavior = scrollBehavior,
            )
        },
        fab = {},
        bottomBarKey = this
    )

    is AppRoute.AddCourses -> ScreenChrome(
        title = "AddCourses",
        topBar = { backstack, scrollBehavior ->
            TopAppBar(title = {
                Text(text = "Add a Course")
            }, navigationIcon = {
                IconButton(onClick = { backstack.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }, scrollBehavior = scrollBehavior)
        },
        fab = {},
        bottomBarKey = null
    )

    is AppRoute.CourseDetails -> {
        val courseId = this.courseId
        ScreenChrome(
            title = "Course Details",
            topBar = { backstack, scrollBehavior ->
                TopAppBar(
                    title = { Text(text = "Course") },
                    navigationIcon = {
                        IconButton(onClick = { backstack.back() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            backstack.navigateTo(AppRoute.CourseEditor(courseId))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit course")
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            fab = {},
            bottomBarKey = null
        )
    }

    is AppRoute.CourseEditor -> ScreenChrome(
        title = "Edit course",
        topBar = { backstack, scrollBehavior ->
            TopAppBar(
                title = { Text(text = "Edit course") },
                navigationIcon = {
                    IconButton(onClick = { backstack.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        fab = {},
        bottomBarKey = null
    )

}