package com.abrarshakhi.selfattention.feature.course.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.core.designsystem.component.LoadingContent
import com.abrarshakhi.selfattention.core.designsystem.component.NavigateUpButton
import com.abrarshakhi.selfattention.core.designsystem.component.SectionHeader
import com.abrarshakhi.selfattention.core.model.Course
import com.abrarshakhi.selfattention.core.model.meetsOn
import com.abrarshakhi.selfattention.core.ui.format.clockLabel
import com.abrarshakhi.selfattention.feature.course.detail.component.AttendanceCalendar
import com.abrarshakhi.selfattention.feature.course.detail.component.CourseHero
import com.abrarshakhi.selfattention.feature.course.detail.component.MarkAttendanceSheet
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CourseDetailScreen(
    courseId: Long,
    onNavigateUp: () -> Unit,
    onEdit: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val locale = LocalLocale.current.platformLocale

    LaunchedEffect(courseId) { viewModel.load(courseId) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(state.course?.name.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                subtitle = state.course?.let { course -> { Text(course.scheduleLabel(locale)) } },
                navigationIcon = { NavigateUpButton(onClick = onNavigateUp) },
                actions = {
                    FilledTonalIconButton(
                        onClick = onEdit,
                        enabled = state.course != null,
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit course")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        val course = state.course
        if (course == null) {
            LoadingContent(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.stats?.let { CourseHero(stats = it) }
            SectionHeader(title = "Calendar")
            AttendanceCalendar(
                month = state.currentMonth,
                weekStart = state.weekStartDay,
                records = state.records,
                isClassDay = { course.meetsOn(it, state.weeklyHolidays) },
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onDayClick = viewModel::openSheet,
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    state.sheetDate?.let { date ->
        MarkAttendanceSheet(
            date = date,
            status = state.records[date]?.status,
            onSelect = viewModel::mark,
            onDismiss = viewModel::closeSheet,
        )
    }
}

private fun Course.scheduleLabel(locale: Locale): String {
    val days = scheduleDays.sorted().joinToString(" ") { it.getDisplayName(TextStyle.SHORT, locale) }
    return listOf(code, days, classTime.clockLabel()).filter { it.isNotBlank() }.joinToString("  ·  ")
}
