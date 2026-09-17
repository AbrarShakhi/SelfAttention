package com.abrarshakhi.selfattention.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Material 3 tonal palette for the app.
 *
 * Seed `#3A6FA3` → HCT hue 252.687, chroma 40.051, tone 45.514, expanded with the standard
 * **TonalSpot** scheme: primary c36, secondary c16, tertiary h+60 c24, neutral c6,
 * neutralVariant c8. The error ramp is the seed-independent M3 baseline (hue 25, chroma 84).
 *
 * Primary chroma is held at 36 rather than the legacy `max(48, sourceChroma)`: at c48 tone 40
 * comes out `#03629F`, a vivid cyan that reads as a different brand. At c36 it is `#34618E`,
 * a near-sibling of the original.
 *
 * Every value carries its palette and tone so the ramp stays auditable. Do not introduce raw
 * colors at call sites — reach these through `MaterialTheme.colorScheme`.
 */

// ── light ────────────────────────────────────────────────────────────────────
internal val primaryLight = Color(0xFF34618E)                 // primary        T40
internal val onPrimaryLight = Color(0xFFFFFFFF)               // primary        T100
internal val primaryContainerLight = Color(0xFFCFE4FF)        // primary        T90
internal val onPrimaryContainerLight = Color(0xFF001D36)      // primary        T10
internal val inversePrimaryLight = Color(0xFF9FCAFD)          // primary        T80
internal val secondaryLight = Color(0xFF525F70)               // secondary      T40
internal val onSecondaryLight = Color(0xFFFFFFFF)             // secondary      T100
internal val secondaryContainerLight = Color(0xFFD7E4F8)      // secondary      T90
internal val onSecondaryContainerLight = Color(0xFF0F1C2B)    // secondary      T10
internal val tertiaryLight = Color(0xFF6A5778)                // tertiary       T40
internal val onTertiaryLight = Color(0xFFFFFFFF)              // tertiary       T100
internal val tertiaryContainerLight = Color(0xFFF3DAFF)       // tertiary       T90
internal val onTertiaryContainerLight = Color(0xFF241432)     // tertiary       T10
internal val errorLight = Color(0xFFBA1A1A)                   // error          T40
internal val onErrorLight = Color(0xFFFFFFFF)                 // error          T100
internal val errorContainerLight = Color(0xFFFFDAD6)          // error          T90
internal val onErrorContainerLight = Color(0xFF410002)        // error          T10
internal val backgroundLight = Color(0xFFF8F9FF)              // neutral        T98
internal val onBackgroundLight = Color(0xFF191C20)            // neutral        T10
internal val surfaceLight = Color(0xFFF8F9FF)                 // neutral        T98
internal val onSurfaceLight = Color(0xFF191C20)               // neutral        T10
internal val surfaceVariantLight = Color(0xFFDFE3EC)          // neutralVariant T90
internal val onSurfaceVariantLight = Color(0xFF42474E)        // neutralVariant T30
internal val outlineLight = Color(0xFF73777F)                 // neutralVariant T50
internal val outlineVariantLight = Color(0xFFC3C7CF)          // neutralVariant T80
internal val scrimLight = Color(0xFF000000)                   // neutral        T0
internal val inverseSurfaceLight = Color(0xFF2E3135)          // neutral        T20
internal val inverseOnSurfaceLight = Color(0xFFEFF0F6)        // neutral        T95
internal val surfaceBrightLight = Color(0xFFF8F9FF)           // neutral        T98
internal val surfaceDimLight = Color(0xFFD8DAE0)              // neutral        T87
internal val surfaceContainerLowestLight = Color(0xFFFFFFFF)  // neutral        T100
internal val surfaceContainerLowLight = Color(0xFFF2F4FA)     // neutral        T96
internal val surfaceContainerLight = Color(0xFFECEDF3)        // neutral        T94
internal val surfaceContainerHighLight = Color(0xFFE6E8EE)    // neutral        T92
internal val surfaceContainerHighestLight = Color(0xFFE0E2E8) // neutral        T90

// ── dark ─────────────────────────────────────────────────────────────────────
internal val primaryDark = Color(0xFF9FCAFD)                  // primary        T80
internal val onPrimaryDark = Color(0xFF003258)                // primary        T20
internal val primaryContainerDark = Color(0xFF174974)         // primary        T30
internal val onPrimaryContainerDark = Color(0xFFCFE4FF)       // primary        T90
internal val inversePrimaryDark = Color(0xFF34618E)           // primary        T40
internal val secondaryDark = Color(0xFFBAC7DA)                // secondary      T80
internal val onSecondaryDark = Color(0xFF243140)              // secondary      T20
internal val secondaryContainerDark = Color(0xFF3B4857)       // secondary      T30
internal val onSecondaryContainerDark = Color(0xFFD7E4F8)     // secondary      T90
internal val tertiaryDark = Color(0xFFD6BEE5)                 // tertiary       T80
internal val onTertiaryDark = Color(0xFF3A2948)               // tertiary       T20
internal val tertiaryContainerDark = Color(0xFF524060)        // tertiary       T30
internal val onTertiaryContainerDark = Color(0xFFF3DAFF)      // tertiary       T90
internal val errorDark = Color(0xFFFFB4AB)                    // error          T80
internal val onErrorDark = Color(0xFF690005)                  // error          T20
internal val errorContainerDark = Color(0xFF93000A)           // error          T30
internal val onErrorContainerDark = Color(0xFFFFDAD6)         // error          T90
internal val backgroundDark = Color(0xFF111418)               // neutral        T6
internal val onBackgroundDark = Color(0xFFE0E2E8)             // neutral        T90
internal val surfaceDark = Color(0xFF111418)                  // neutral        T6
internal val onSurfaceDark = Color(0xFFE0E2E8)                // neutral        T90
internal val surfaceVariantDark = Color(0xFF42474E)           // neutralVariant T30
internal val onSurfaceVariantDark = Color(0xFFC3C7CF)         // neutralVariant T80
internal val outlineDark = Color(0xFF8D9199)                  // neutralVariant T60
internal val outlineVariantDark = Color(0xFF42474E)           // neutralVariant T30
internal val scrimDark = Color(0xFF000000)                    // neutral        T0
internal val inverseSurfaceDark = Color(0xFFE0E2E8)           // neutral        T90
internal val inverseOnSurfaceDark = Color(0xFF2E3135)         // neutral        T20
internal val surfaceBrightDark = Color(0xFF36393E)            // neutral        T24
internal val surfaceDimDark = Color(0xFF111418)               // neutral        T6
internal val surfaceContainerLowestDark = Color(0xFF0B0E12)   // neutral        T4
internal val surfaceContainerLowDark = Color(0xFF191C20)      // neutral        T10
internal val surfaceContainerDark = Color(0xFF1D2024)         // neutral        T12
internal val surfaceContainerHighDark = Color(0xFF272A2F)     // neutral        T17
internal val surfaceContainerHighestDark = Color(0xFF323539)  // neutral        T22

// ── fixed (identical in both schemes by definition) ──────────────────────────
internal val primaryFixed = Color(0xFFCFE4FF)                 // primary        T90
internal val primaryFixedDim = Color(0xFF9FCAFD)              // primary        T80
internal val onPrimaryFixed = Color(0xFF001D36)               // primary        T10
internal val onPrimaryFixedVariant = Color(0xFF174974)        // primary        T30
internal val secondaryFixed = Color(0xFFD7E4F8)               // secondary      T90
internal val secondaryFixedDim = Color(0xFFBAC7DA)            // secondary      T80
internal val onSecondaryFixed = Color(0xFF0F1C2B)             // secondary      T10
internal val onSecondaryFixedVariant = Color(0xFF3B4857)      // secondary      T30
internal val tertiaryFixed = Color(0xFFF3DAFF)                // tertiary       T90
internal val tertiaryFixedDim = Color(0xFFD6BEE5)             // tertiary       T80
internal val onTertiaryFixed = Color(0xFF241432)              // tertiary       T10
internal val onTertiaryFixedVariant = Color(0xFF524060)       // tertiary       T30
