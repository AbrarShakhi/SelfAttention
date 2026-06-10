package com.abrarshakhi.selfattention.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.abrarshakhi.selfattention.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val CaveatFamily = FontFamily(
    Font(GoogleFont("Caveat"), provider, weight = FontWeight.Normal),
    Font(GoogleFont("Caveat"), provider, weight = FontWeight.SemiBold),
    Font(GoogleFont("Caveat"), provider, weight = FontWeight.Bold),
)

val KalamFamily = FontFamily(
    Font(GoogleFont("Kalam"), provider, weight = FontWeight.Light),
    Font(GoogleFont("Kalam"), provider, weight = FontWeight.Normal),
    Font(GoogleFont("Kalam"), provider, weight = FontWeight.Bold),
)

val ArchitectsDaughterFamily = FontFamily(
    Font(GoogleFont("Architects Daughter"), provider, weight = FontWeight.Normal),
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.Bold, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.SemiBold, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.Normal, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = CaveatFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = KalamFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = ArchitectsDaughterFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = ArchitectsDaughterFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = ArchitectsDaughterFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp),
)
