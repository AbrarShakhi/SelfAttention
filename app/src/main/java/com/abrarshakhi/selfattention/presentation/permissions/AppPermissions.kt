package com.abrarshakhi.selfattention.presentation.permissions

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Runtime state of the permissions the reminder pipeline depends on.
 *
 * Without these the app schedules alarms and posts notifications that the system silently drops —
 * `NotificationHelper.hasPermission()` and `AlarmSchedulerImpl.canScheduleExact()` both return
 * early. These holders exist so the UI can say so and offer a way out.
 */
@Immutable
data class PermissionState(
    val isGranted: Boolean,
    /** True once the system will no longer show its dialog; the only route left is app settings. */
    val mustUseSettings: Boolean,
    val request: () -> Unit,
)

/**
 * `POST_NOTIFICATIONS`, required from API 33. Below that it is granted at install time.
 *
 * Re-reads on every resume so returning from system settings updates the UI.
 */
@Composable
fun rememberNotificationPermissionState(): PermissionState {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val needsRuntimeGrant = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var granted by remember { mutableStateOf(context.hasNotificationPermission()) }
    var asked by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { result ->
        granted = result
        asked = true
    }

    OnResume { granted = context.hasNotificationPermission() }

    // After a denial the system stops showing the dialog and rationale goes false; from then on
    // launching the request is a silent no-op, so send the user to app settings instead.
    val mustUseSettings = needsRuntimeGrant && asked && !granted &&
        activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false

    return PermissionState(
        isGranted = granted,
        mustUseSettings = mustUseSettings,
        request = {
            when {
                !needsRuntimeGrant -> Unit
                mustUseSettings -> context.openAppSettings()
                else -> launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
    )
}

/**
 * Exact alarm scheduling, restricted from API 31.
 *
 * There is no runtime dialog for this one — the user has to flip it in system settings. The app
 * also declares `USE_EXACT_ALARM`, which is auto-granted from API 33, so in practice this only
 * comes up on API 31–32 where `SCHEDULE_EXACT_ALARM` can be revoked.
 */
@Composable
fun rememberExactAlarmPermissionState(): PermissionState {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(context.canScheduleExactAlarms()) }

    OnResume { granted = context.canScheduleExactAlarms() }

    return PermissionState(
        isGranted = granted,
        mustUseSettings = true,
        request = { context.openExactAlarmSettings() },
    )
}

// ── plumbing ─────────────────────────────────────────────────────────────────

@Composable
private fun OnResume(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

fun Context.canScheduleExactAlarms(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    } else {
        true
    }

private fun Context.openAppSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

private fun Context.openExactAlarmSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                .setData(Uri.fromParts("package", packageName, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
