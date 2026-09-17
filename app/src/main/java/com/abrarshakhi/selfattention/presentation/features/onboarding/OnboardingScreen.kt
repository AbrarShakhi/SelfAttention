package com.abrarshakhi.selfattention.presentation.features.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abrarshakhi.selfattention.domain.model.AppFont
import com.abrarshakhi.selfattention.domain.model.AppSettings
import com.abrarshakhi.selfattention.domain.model.ThemeMode
import com.abrarshakhi.selfattention.presentation.components.FontSelector
import com.abrarshakhi.selfattention.presentation.components.ThemeModeSelector
import com.abrarshakhi.selfattention.presentation.components.WeekStartSelector
import com.abrarshakhi.selfattention.presentation.components.WeeklyHolidaySelector
import com.abrarshakhi.selfattention.presentation.features.settings.SettingsViewModel
import com.abrarshakhi.selfattention.presentation.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek

private const val PageCount = 4


@Composable
fun OnboardingScreen(
    settingsViewModel: SettingsViewModel,
    onFinish: () -> Unit,
    onAddCourse: () -> Unit,
) {
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    val settings = state.settings
    val pagerState = rememberPagerState(pageCount = { PageCount })
    val scope = rememberCoroutineScope()

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? -> uri?.let(settingsViewModel::importFrom) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                // Always available: setup is optional and every choice can be changed later.
                TextButton(onClick = onFinish) { Text("Skip") }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (page) {
                        0 -> WelcomePage()
                        1 -> AppearancePage(
                            settings = settings,
                            onThemeModeChange = settingsViewModel::setThemeMode,
                            onAppFontChange = settingsViewModel::setAppFont,
                        )

                        2 -> WeekPage(
                            settings = settings,
                            onWeekStartChange = settingsViewModel::setWeekStartDay,
                            onHolidayToggle = settingsViewModel::toggleHoliday,
                        )

                        else -> CoursesPage(
                            onAddCourse = onAddCourse,
                            onImport = {
                                importLauncher.launch(
                                    arrayOf(
                                        "application/json",
                                        "*/*"
                                    )
                                )
                            },
                        )
                    }
                }
            }

            PageIndicator(
                pageCount = PageCount,
                current = pagerState.currentPage,
                modifier = Modifier.padding(vertical = 12.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        },
                        modifier = Modifier.weight(1f),
                    ) { Text("Back") }
                }
                Button(
                    onClick = {
                        if (pagerState.currentPage == PageCount - 1) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (pagerState.currentPage == PageCount - 1) "Get started" else "Next")
                }
            }
        }
    }

    state.backupMessage?.let { message ->
        AlertDialog(
            onDismissRequest = settingsViewModel::dismissBackupMessage,
            icon = {
                Icon(
                    imageVector = if (message.isError) {
                        Icons.Default.ErrorOutline
                    } else {
                        Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (message.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        AppTheme.status.present.color
                    },
                )
            },
            title = { Text(message.title) },
            text = { Text(message.body) },
            confirmButton = {
                TextButton(onClick = settingsViewModel::dismissBackupMessage) { Text("OK") }
            },
        )
    }
}

// ── pages ────────────────────────────────────────────────────────────────────

@Composable
private fun WelcomePage() {
    Spacer(Modifier.height(24.dp))
    Text(
        text = "Self Attention",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = "Keep track of the classes you actually attend.",
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(Modifier.height(8.dp))
    Bullet("Add each course with its days and time.")
    Bullet("Get a reminder before class, and a nudge afterwards to mark whether you went.")
    Bullet("Watch your attendance percentage per course, so you know where you stand.")
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Next, a few quick preferences. You can change any of them later in Settings.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun Bullet(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("•", style = MaterialTheme.typography.bodyLarge)
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AppearancePage(
    settings: AppSettings,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAppFontChange: (AppFont) -> Unit,
) {
    PageHeading("Appearance", "Changes apply as you pick them.")
    ThemeModeSelector(selected = settings.themeMode, onSelect = onThemeModeChange)
    FontSelector(selected = settings.appFont, onSelect = onAppFontChange)
}

@Composable
private fun WeekPage(
    settings: AppSettings,
    onWeekStartChange: (DayOfWeek) -> Unit,
    onHolidayToggle: (DayOfWeek) -> Unit,
) {
    PageHeading("Your week", "Weekly holidays never count as class days.")
    WeekStartSelector(selected = settings.weekStartDay, onSelect = onWeekStartChange)
    Text(
        text = "Weekly holidays",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
    WeeklyHolidaySelector(selected = settings.weeklyHolidays, onToggle = onHolidayToggle)
}

@Composable
private fun CoursesPage(onAddCourse: () -> Unit, onImport: () -> Unit) {
    PageHeading("Add your courses", "Or bring them in from a backup — you can also do this later.")
    Button(onClick = onAddCourse, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.height(0.dp))
        Text("  Add a course")
    }
    OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.Download, contentDescription = null)
        Spacer(Modifier.height(0.dp))
        Text("  Import from a backup")
    }
    Text(
        text = "Importing adds courses from a JSON file exported by Self Attention.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PageHeading(title: String, subtitle: String) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun PageIndicator(pageCount: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val color by animateColorAsState(
                targetValue = if (index == current) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
                label = "onboardingDot",
            )
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                Surface(
                    modifier = Modifier.size(if (index == current) 10.dp else 8.dp),
                    shape = CircleShape,
                    color = color,
                    content = {},
                )
            }
        }
    }
}
