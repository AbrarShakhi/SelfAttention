package com.abrarshakhi.selfattention.feature.settings.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AlarmOff
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abrarshakhi.selfattention.core.designsystem.component.SectionHeader
import com.abrarshakhi.selfattention.core.designsystem.component.SegmentedGroup
import com.abrarshakhi.selfattention.core.designsystem.component.SegmentedPanel
import com.abrarshakhi.selfattention.core.designsystem.component.ShapedIcon
import com.abrarshakhi.selfattention.core.designsystem.component.segmentedItemColors
import com.abrarshakhi.selfattention.core.designsystem.theme.AppTheme
import com.abrarshakhi.selfattention.core.designsystem.theme.supportsWallpaperColors
import com.abrarshakhi.selfattention.core.model.AppFont
import com.abrarshakhi.selfattention.core.model.AppSettings
import com.abrarshakhi.selfattention.core.model.ColorStyle
import com.abrarshakhi.selfattention.core.model.ThemeMode
import com.abrarshakhi.selfattention.core.ui.permission.PermissionState
import com.abrarshakhi.selfattention.core.ui.preference.ColorStyleSelector
import com.abrarshakhi.selfattention.core.ui.preference.FontSelector
import com.abrarshakhi.selfattention.core.ui.preference.SeedColorPicker
import com.abrarshakhi.selfattention.core.ui.preference.ThemeModeSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeekStartSelector
import com.abrarshakhi.selfattention.core.ui.preference.WeeklyHolidaySelector
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RemindersSection(notifications: PermissionState, exactAlarms: PermissionState) {
    SectionHeader(title = "Reminders")
    SegmentedGroup(
        items = listOf(
            { shapes ->
                PermissionItem(
                    shapes = shapes,
                    state = notifications,
                    grantedIcon = Icons.Rounded.Notifications,
                    deniedIcon = Icons.Rounded.NotificationsOff,
                    title = "Notifications",
                    grantedText = "Reminders and attendance prompts can appear",
                    deniedText = if (notifications.mustUseSettings) "Blocked — tap to open settings" else "Tap to allow",
                )
            },
            { shapes ->
                PermissionItem(
                    shapes = shapes,
                    state = exactAlarms,
                    grantedIcon = Icons.Rounded.Alarm,
                    deniedIcon = Icons.Rounded.AlarmOff,
                    title = "Exact alarms",
                    grantedText = "Reminders fire right on time",
                    deniedText = "Tap to allow, or reminders may arrive late",
                )
            },
        ),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PermissionItem(
    shapes: ListItemShapes,
    state: PermissionState,
    grantedIcon: ImageVector,
    deniedIcon: ImageVector,
    title: String,
    grantedText: String,
    deniedText: String,
) {
    val palette = if (state.isGranted) AppTheme.status.present else AppTheme.status.absent
    SegmentedListItem(
        onClick = { if (!state.isGranted) state.request() },
        shapes = shapes,
        colors = segmentedItemColors(),
        leadingContent = {
            ShapedIcon(
                icon = if (state.isGranted) grantedIcon else deniedIcon,
                polygon = if (state.isGranted) MaterialShapes.Cookie6Sided else MaterialShapes.Sunny,
                containerColor = palette.colorContainer,
                contentColor = palette.onColorContainer,
                size = 40.dp,
            )
        },
        supportingContent = { Text(if (state.isGranted) grantedText else deniedText) },
    ) { Text(title) }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceSection(
    settings: AppSettings,
    onThemeModeChange: (ThemeMode) -> Unit,
    onUseWallpaperColorsChange: (Boolean) -> Unit,
    onSeedColorChange: (Int) -> Unit,
    onColorStyleChange: (ColorStyle) -> Unit,
    onPureBlackChange: (Boolean) -> Unit,
    onAppFontChange: (AppFont) -> Unit,
) {
    val colors = settings.colorPreferences
    SectionHeader(title = "Appearance")
    SegmentedGroup(
        items = buildList {
            add { shapes ->
                SegmentedPanel(shapes = shapes, title = "Theme") {
                    ThemeModeSelector(selected = settings.themeMode, onSelect = onThemeModeChange)
                }
            }
            if (supportsWallpaperColors) {
                add { shapes ->
                    SwitchItem(
                        shapes = shapes,
                        icon = Icons.Rounded.Wallpaper,
                        title = "Wallpaper colours",
                        supporting = "Match the colours of your wallpaper",
                        checked = colors.useWallpaperColors,
                        onCheckedChange = onUseWallpaperColorsChange,
                    )
                }
            }
            add { shapes ->
                SegmentedPanel(
                    shapes = shapes,
                    title = "Colour",
                    supportingText = if (colors.useWallpaperColors && supportsWallpaperColors) {
                        "Turn off wallpaper colours to pick your own"
                    } else {
                        "Pick a seed and a palette style"
                    },
                ) {
                    val enabled = !(colors.useWallpaperColors && supportsWallpaperColors)
                    SeedColorPicker(selected = colors.seedColor, onSelect = onSeedColorChange, enabled = enabled)
                    ColorStyleSelector(selected = colors.style, onSelect = onColorStyleChange, enabled = enabled)
                }
            }
            add { shapes ->
                SwitchItem(
                    shapes = shapes,
                    icon = Icons.Rounded.Contrast,
                    title = "Pure black",
                    supporting = "Use a black background in dark theme",
                    checked = colors.pureBlack,
                    onCheckedChange = onPureBlackChange,
                )
            }
            add { shapes ->
                SegmentedPanel(shapes = shapes, title = "Font", supportingText = settings.appFont.label) {
                    FontSelector(selected = settings.appFont, onSelect = onAppFontChange)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SwitchItem(
    shapes: ListItemShapes,
    icon: ImageVector,
    title: String,
    supporting: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SegmentedListItem(
        checked = checked,
        onCheckedChange = onCheckedChange,
        shapes = shapes,
        colors = segmentedItemColors(),
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        supportingContent = { Text(supporting) },
    ) { Text(title) }
}

@Composable
fun WeekSection(
    settings: AppSettings,
    onWeekStartChange: (DayOfWeek) -> Unit,
    onHolidayToggle: (DayOfWeek) -> Unit,
) {
    SectionHeader(title = "Week")
    SegmentedGroup(
        items = listOf(
            { shapes ->
                SegmentedPanel(shapes = shapes, title = "First day of the week") {
                    WeekStartSelector(selected = settings.weekStartDay, onSelect = onWeekStartChange)
                }
            },
            { shapes ->
                SegmentedPanel(
                    shapes = shapes,
                    title = "Weekly holidays",
                    supportingText = "Classes on these days are never counted",
                ) {
                    WeeklyHolidaySelector(
                        selected = settings.weeklyHolidays,
                        onToggle = onHolidayToggle,
                        weekStart = settings.weekStartDay,
                    )
                }
            },
        ),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackupSection(onExport: (String) -> Unit, onImport: (String) -> Unit) {
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let { onExport(it.toString()) } }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let { onImport(it.toString()) } }
    val defaultName = remember {
        "self-attention-backup-${LocalDate.now().format(DateTimeFormatter.ISO_DATE)}.json"
    }

    SectionHeader(title = "Backup")
    SegmentedGroup(
        items = listOf(
            { shapes ->
                SegmentedListItem(
                    onClick = { exportLauncher.launch(defaultName) },
                    shapes = shapes,
                    colors = segmentedItemColors(),
                    leadingContent = { Icon(Icons.Rounded.Upload, contentDescription = null) },
                    supportingContent = { Text("Save every course and attendance record to a file") },
                ) { Text("Export") }
            },
            { shapes ->
                SegmentedListItem(
                    onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                    shapes = shapes,
                    colors = segmentedItemColors(),
                    leadingContent = { Icon(Icons.Rounded.Download, contentDescription = null) },
                    supportingContent = { Text("Add courses from a backup — nothing is overwritten") },
                ) { Text("Import") }
            },
        ),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutSection() {
    val context = LocalContext.current
    val version = remember(context) {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull()
            .orEmpty()
    }
    SectionHeader(title = "About")
    SegmentedGroup(
        items = listOf(
            { shapes ->
                SegmentedListItem(
                    shapes = shapes,
                    colors = segmentedItemColors(),
                    leadingContent = { Icon(Icons.Rounded.Info, contentDescription = null) },
                    supportingContent = { Text("Version $version") },
                ) { Text("Self Attention") }
            },
        ),
    )
}
