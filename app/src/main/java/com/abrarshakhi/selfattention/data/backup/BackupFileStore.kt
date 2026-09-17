package com.abrarshakhi.selfattention.data.backup

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Reads and writes backup files chosen through the Storage Access Framework.
 *
 * Keeps `Context` out of the ViewModel, and keeps the IO off the main thread.
 */
class BackupFileStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun read(uri: Uri): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
            ?: error("Could not open that file")
    }

    suspend fun write(uri: Uri, text: String) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { it.write(text.encodeToByteArray()) }
            ?: error("Could not write to that file")
    }
}
