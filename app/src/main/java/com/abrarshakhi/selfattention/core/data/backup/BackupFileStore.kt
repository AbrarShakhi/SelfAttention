package com.abrarshakhi.selfattention.core.data.backup

interface BackupFileStore {
    suspend fun read(uri: String): String
    suspend fun write(uri: String, text: String)
}
