package com.abrarshakhi.selfattention.feature.widget.attendance

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap

internal fun attendanceRing(sizePx: Int, strokePx: Float, progress: Float, trackColor: Int, progressColor: Int): Bitmap {
    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)
    val inset = strokePx / 2f
    val bounds = RectF(inset, inset, sizePx - inset, sizePx - inset)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokePx
        strokeCap = Paint.Cap.ROUND
    }
    paint.color = trackColor
    canvas.drawArc(bounds, 0f, 360f, false, paint)
    if (progress > 0f) {
        paint.color = progressColor
        canvas.drawArc(bounds, -90f, 360f * progress.coerceIn(0f, 1f), false, paint)
    }
    return bitmap
}
