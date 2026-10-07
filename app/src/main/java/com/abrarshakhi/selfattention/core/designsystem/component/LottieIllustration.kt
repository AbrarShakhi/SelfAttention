package com.abrarshakhi.selfattention.core.designsystem.component

import androidx.annotation.RawRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun LottieIllustration(
    @RawRes animation: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    val scheme = MaterialTheme.colorScheme
    val dynamicProperties = rememberLottieDynamicProperties(
        *tint("primary", scheme.primary),
        *tint("onPrimary", scheme.onPrimary),
        *tint("primaryContainer", scheme.primaryContainer),
        *tint("secondary", scheme.secondary),
        *tint("secondaryContainer", scheme.secondaryContainer),
        *tint("tertiary", scheme.tertiary),
        *tint("tertiaryContainer", scheme.tertiaryContainer),
        *tint("surface", scheme.surfaceContainerHighest),
    )
    LottieAnimation(
        composition = composition,
        modifier = modifier,
        iterations = iterations,
        dynamicProperties = dynamicProperties,
    )
}

@Composable
private fun tint(layer: String, color: Color): Array<LottieDynamicProperty<*>> {
    val argb = color.toArgb()
    return arrayOf(
        rememberLottieDynamicProperty(LottieProperty.COLOR, argb, layer, "**"),
        rememberLottieDynamicProperty(LottieProperty.STROKE_COLOR, argb, layer, "**"),
    )
}
