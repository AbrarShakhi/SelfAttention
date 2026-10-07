package com.abrarshakhi.selfattention.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.model.AppFont

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val DownloadedWeights = listOf(
    FontWeight.Normal,
    FontWeight.Medium,
    FontWeight.SemiBold,
    FontWeight.Bold,
    FontWeight.ExtraBold,
)

fun fontFamilyFor(font: AppFont): FontFamily {
    if (!font.isDownloadable) return FontFamily.Default
    val googleFont = GoogleFont(font.label)
    return FontFamily(
        DownloadedWeights.map { weight ->
            Font(googleFont = googleFont, fontProvider = provider, weight = weight)
        },
    )
}

fun appTypography(font: AppFont): Typography = Typography(fontFamily = fontFamilyFor(font))
