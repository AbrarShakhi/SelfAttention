package com.abrarshakhi.selfattention.core.domain.backup

sealed interface ImportResult {
    data class Success(val courses: Int, val attendance: Int) : ImportResult
    data class Failure(val message: String) : ImportResult
}

sealed interface ExportResult {
    data object Success : ExportResult
    data class Failure(val message: String) : ExportResult
}
