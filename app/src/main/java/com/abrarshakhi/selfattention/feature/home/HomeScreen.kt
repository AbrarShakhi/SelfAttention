package com.abrarshakhi.selfattention.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.designsystem.component.EmptyState
import com.abrarshakhi.selfattention.core.designsystem.component.LoadingContent
import com.abrarshakhi.selfattention.core.designsystem.component.SectionHeader
import com.abrarshakhi.selfattention.core.model.AttendanceStatus
import com.abrarshakhi.selfattention.core.model.ScheduledClass
import com.abrarshakhi.selfattention.core.ui.format.greetingFor
import com.abrarshakhi.selfattention.core.ui.format.longLabel
import com.abrarshakhi.selfattention.feature.home.component.CourseCard
import com.abrarshakhi.selfattention.feature.home.component.NextClassCard
import com.abrarshakhi.selfattention.feature.home.component.OverviewCard
import com.abrarshakhi.selfattention.feature.home.component.TodayClassCard

@Composable
fun HomeScreen(
    onCourseClick: (Long) -> Unit,
    onAddCourse: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onCourseClick = onCourseClick,
        onAddCourse = onAddCourse,
        onMark = viewModel::mark,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    onCourseClick: (Long) -> Unit,
    onAddCourse: () -> Unit,
    onMark: (ScheduledClass, AttendanceStatus?) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val fabExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { HomeTopAppBar(state = state, scrollBehavior = scrollBehavior) },
        floatingActionButton = {
            if (state.courses.isNotEmpty()) {
                MediumExtendedFloatingActionButton(
                    text = { Text("Add course") },
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    onClick = onAddCourse,
                    expanded = fabExpanded,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.isLoading,
            modifier = Modifier.padding(innerPadding),
            label = "homeLoading",
        ) { loading ->
            when {
                loading -> LoadingContent()
                state.courses.isEmpty() -> HomeEmpty(onAddCourse = onAddCourse)
                else -> HomeList(
                    state = state,
                    listState = listState,
                    onCourseClick = onCourseClick,
                    onMark = onMark,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeTopAppBar(state: HomeUiState, scrollBehavior: TopAppBarScrollBehavior) {
    val locale = LocalLocale.current.platformLocale
    LargeFlexibleTopAppBar(
        title = { Text(greetingFor(state.now.toLocalTime())) },
        subtitle = { Text(state.now.toLocalDate().longLabel(locale)) },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun HomeList(
    state: HomeUiState,
    listState: LazyListState,
    onCourseClick: (Long) -> Unit,
    onMark: (ScheduledClass, AttendanceStatus?) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "overview") {
            OverviewCard(stats = state.overall, modifier = Modifier.animateItem())
        }
        state.nextClass?.let { next ->
            item(key = "next") {
                NextClassCard(
                    nextClass = next,
                    now = state.now,
                    onClick = { onCourseClick(next.course.id) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
        if (state.today.isNotEmpty()) {
            item(key = "today-header") {
                SectionHeader(title = "Today", modifier = Modifier.animateItem())
            }
            items(state.today, key = { "today-${it.course.id}" }) { scheduled ->
                TodayClassCard(
                    scheduled = scheduled,
                    now = state.now,
                    onMark = { onMark(scheduled, it) },
                    onClick = { onCourseClick(scheduled.course.id) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
        item(key = "courses-header") {
            SectionHeader(title = "Your courses", modifier = Modifier.animateItem())
        }
        items(state.courses, key = { "course-${it.course.id}" }) { summary ->
            CourseCard(
                course = summary.course,
                stats = summary.stats,
                onClick = { onCourseClick(summary.course.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeEmpty(onAddCourse: () -> Unit) {
    EmptyState(
        animation = R.raw.empty_courses,
        title = "No courses yet",
        message = "Add your first course and Self Attention will remind you before class and keep score for you.",
        modifier = Modifier.padding(top = 32.dp),
        illustrationSize = 220.dp,
        action = {
            Button(
                onClick = onAddCourse,
                shapes = ButtonDefaults.shapes(),
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Text("Add a course", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
            }
        },
    )
}
