package com.abrarshakhi.selfattention.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.abrarshakhi.selfattention.R
import com.abrarshakhi.selfattention.core.model.AppFont

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

fun fontFamilyFor(font: AppFont): FontFamily {
    if (!font.isDownloadable) return FontFamily.Default
    val googleFont = GoogleFont(font.label)
    return FontFamily(
        Font(googleFont = googleFont, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = googleFont, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = googleFont, fontProvider = provider, weight = FontWeight.SemiBold),
        Font(googleFont = googleFont, fontProvider = provider, weight = FontWeight.Bold),
    )
}

fun appTypography(font: AppFont): Typography {
    val family = fontFamilyFor(font)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Medium,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Normal,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Medium,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = family, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
        ),
    )
}
