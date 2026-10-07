package com.abrarshakhi.selfattention.core.data.backup

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class ContentResolverBackupFileStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : BackupFileStore {

    override suspend fun read(uri: String): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri.toUri())
            ?.use { it.readBytes().decodeToString() }
            ?: throw IOException("Could not open that file")
    }

    override suspend fun write(uri: String, text: String) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri.toUri())
            ?.use { it.write(text.encodeToByteArray()) }
            ?: throw IOException("Could not write to that file")
    }
}
