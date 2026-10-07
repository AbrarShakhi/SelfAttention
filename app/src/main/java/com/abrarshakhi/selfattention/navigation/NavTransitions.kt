package com.abrarshakhi.selfattention.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay

private val SlideSpring = spring<IntOffset>(
    dampingRatio = 0.9f,
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold,
)
private val FadeSpring = spring<Float>(stiffness = Spring.StiffnessMedium)

internal fun <T : Any> AnimatedContentTransitionScope<Scene<T>>.forwardTransition(): ContentTransform =
    (slideInHorizontally(SlideSpring) { it / 5 } + fadeIn(FadeSpring)) togetherWith
        (slideOutHorizontally(SlideSpring) { -it / 10 } + fadeOut(FadeSpring))

internal fun <T : Any> AnimatedContentTransitionScope<Scene<T>>.backwardTransition(): ContentTransform =
    (slideInHorizontally(SlideSpring) { -it / 10 } + fadeIn(FadeSpring)) togetherWith
        (slideOutHorizontally(SlideSpring) { it / 5 } + fadeOut(FadeSpring))

internal val FadeThroughMetadata: Map<String, Any> = NavDisplay.transitionSpec {
    (fadeIn(FadeSpring) + scaleIn(spring(stiffness = Spring.StiffnessMediumLow), initialScale = 0.96f)) togetherWith
        fadeOut(FadeSpring)
}
