package com.abrarshakhi.selfattention.core.ui.backup

import com.abrarshakhi.selfattention.core.domain.backup.ExportResult
import com.abrarshakhi.selfattention.core.domain.backup.ImportResult

data class BackupMessage(val title: String, val body: String, val isError: Boolean)

fun ImportResult.toBackupMessage(): BackupMessage = when (this) {
    is ImportResult.Success -> BackupMessage(
        title = "Import complete",
        body = "Added $courses course(s) and $attendance attendance record(s).",
        isError = false,
    )
    is ImportResult.Failure -> BackupMessage(
        title = "Couldn't import that file",
        body = message,
        isError = true,
    )
}

fun ExportResult.toBackupMessage(): BackupMessage = when (this) {
    ExportResult.Success -> BackupMessage(
        title = "Backup saved",
        body = "Your courses and attendance have been written to the file you chose.",
        isError = false,
    )
    is ExportResult.Failure -> BackupMessage(
        title = "Export failed",
        body = message,
        isError = true,
    )
}
