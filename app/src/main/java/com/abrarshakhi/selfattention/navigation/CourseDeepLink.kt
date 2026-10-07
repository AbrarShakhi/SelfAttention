package com.abrarshakhi.selfattention.navigation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.abrarshakhi.selfattention.MainActivity

object CourseDeepLink {
    private const val SCHEME = "selfattention"
    private const val HOST = "course"

    fun intent(context: Context, courseId: Long): Intent =
        Intent(context, MainActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .setData("$SCHEME://$HOST/$courseId".toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

    fun courseId(intent: Intent?): Long? {
        val data = intent?.data ?: return null
        if (data.scheme != SCHEME || data.host != HOST) return null
        return data.lastPathSegment?.toLongOrNull()
    }
}
