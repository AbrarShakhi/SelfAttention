package com.abrarshakhi.selfattention.core.ui.attendance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.abrarshakhi.selfattention.core.model.AttendanceStatus

val AttendanceStatus.label: String
    get() = when (this) {
        AttendanceStatus.PRESENT -> "Present"
        AttendanceStatus.ABSENT -> "Absent"
        AttendanceStatus.HOLIDAY -> "Holiday"
    }

val AttendanceStatus.icon: ImageVector
    get() = when (this) {
        AttendanceStatus.PRESENT -> Icons.Rounded.Check
        AttendanceStatus.ABSENT -> Icons.Rounded.Close
        AttendanceStatus.HOLIDAY -> Icons.Rounded.WbSunny
    }
