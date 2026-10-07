package com.abrarshakhi.selfattention.feature.onboarding

import androidx.annotation.RawRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.designsystem.component.LottieIllustration
import com.abrarshakhi.selfattention.core.designsystem.component.ShapedIcon
import com.abrarshakhi.selfattention.core.ui.backup.BackupMessageDialog
import com.abrarshakhi.selfattention.core.ui.preference.ColorStyleSelector
import com.abrarshakhi.selfattention.core.ui.preference.FontSelector
import com.abrarshakhi.selfattention.core.ui.preference.SeedColorPicker
import com.abrarshakhi.selfattention.core.ui.preference.ThemeModeSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeekStartSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeeklyHolidaySelector
import com.abrarshakhi.selfattention.feature.onboarding.component.PageIndicator
import kotlinx.coroutines.launch

private const val PageCount = 4

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onAddCourse: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings = state.settings
    val pagerState = rememberPagerState(pageCount = { PageCount })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == PageCount - 1

    val finish = {
        viewModel.completeOnboarding()
        onFinish()
    }
    val addCourse = {
        viewModel.completeOnboarding()
        onAddCourse()
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.importFrom(it.toString()) }
    }

    Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                AnimatedVisibility(visible = !isLastPage, enter = fadeIn(), exit = fadeOut()) {
                    TextButton(onClick = finish) { Text("Skip") }
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (page) {
                        0 -> WelcomePage()
                        1 -> OnboardingPage(
                            animation = R.raw.welcome,
                            title = "Make it yours",
                            subtitle = "Changes apply as you pick them.",
                        ) {
                            ThemeModeSelector(selected = settings.themeMode, onSelect = viewModel::setThemeMode)
                            SeedColorPicker(selected = settings.colorPreferences.seedColor, onSelect = viewModel::setSeedColor)
                            ColorStyleSelector(selected = settings.colorPreferences.style, onSelect = viewModel::setColorStyle)
                            FontSelector(selected = settings.appFont, onSelect = viewModel::setAppFont)
                        }
                        2 -> OnboardingPage(
                            animation = R.raw.empty_day,
                            title = "Your week",
                            subtitle = "Weekly holidays never count as class days.",
                        ) {
                            Text("First day of the week", style = MaterialTheme.typography.titleSmall)
                            WeekStartSelector(selected = settings.weekStartDay, onSelect = viewModel::setWeekStartDay)
                            Text("Weekly holidays", style = MaterialTheme.typography.titleSmall)
                            WeeklyHolidaySelector(
                                selected = settings.weeklyHolidays,
                                onToggle = viewModel::toggleHoliday,
                                weekStart = settings.weekStartDay,
                            )
                        }
                        else -> OnboardingPage(
                            animation = R.raw.empty_courses,
                            title = "Add your courses",
                            subtitle = "Or bring them in from a backup — you can also do this later.",
                        ) {
                            Button(
                                onClick = addCourse,
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null)
                                Text("Add a course", modifier = Modifier.padding(start = 8.dp))
                            }
                            OutlinedButton(
                                onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Download, contentDescription = null)
                                Text("Import a backup", modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PageIndicator(pageCount = PageCount, current = pagerState.currentPage, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (isLastPage) {
                            finish()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                    shapes = ButtonDefaults.shapes(),
                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                ) {
                    AnimatedContent(
                        targetState = isLastPage,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "onboardingNext",
                    ) { last -> Text(if (last) "Get started" else "Next") }
                    AnimatedVisibility(visible = !isLastPage, enter = expandHorizontally(), exit = shrinkHorizontally()) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp).size(20.dp),
                        )
                    }
                }
            }
        }
    }

    state.backupMessage?.let { BackupMessageDialog(message = it, onDismiss = viewModel::dismissBackupMessage) }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WelcomePage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        LottieIllustration(animation = R.raw.welcome, modifier = Modifier.size(220.dp))
    }
    Text(
        text = "Self Attention",
        style = MaterialTheme.typography.displaySmallEmphasized,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(text = "Keep track of the classes you actually attend.", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(4.dp))
    Feature(Icons.Rounded.School, MaterialShapes.Cookie9Sided, "Add each course with its days and time.")
    Feature(Icons.Rounded.NotificationsActive, MaterialShapes.Clover4Leaf, "Get a reminder before class, and a nudge afterwards to mark whether you went.")
    Feature(Icons.Rounded.Insights, MaterialShapes.Sunny, "Watch your attendance per course, so you know where you stand.")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Feature(icon: ImageVector, polygon: RoundedPolygon, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        ShapedIcon(
            icon = icon,
            polygon = polygon,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            size = 44.dp,
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OnboardingPage(
    @RawRes animation: Int,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        LottieIllustration(animation = animation, modifier = Modifier.size(150.dp))
    }
    Text(text = title, style = MaterialTheme.typography.headlineMediumEmphasized)
    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
}
