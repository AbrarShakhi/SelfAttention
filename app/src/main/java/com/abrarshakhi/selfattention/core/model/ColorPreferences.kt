package com.abrarshakhi.selfattention.core.model

data class ColorPreferences(
    val useWallpaperColors: Boolean = false,
    val seedColor: Int = DEFAULT_SEED_COLOR,
    val style: ColorStyle = ColorStyle.EXPRESSIVE,
    val pureBlack: Boolean = false,
) {
    companion object {
        const val DEFAULT_SEED_COLOR: Int = 0xFF3A6FA3.toInt()
    }
}

enum class ColorStyle(val label: String) {
    TONAL_SPOT("Tonal"),
    EXPRESSIVE("Expressive"),
    VIBRANT("Vibrant"),
    FIDELITY("Fidelity"),
    RAINBOW("Rainbow"),
    MONOCHROME("Mono"),
}
