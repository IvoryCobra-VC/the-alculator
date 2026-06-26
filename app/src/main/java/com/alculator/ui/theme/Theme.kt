package com.alculator.ui.theme

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// — Light limewash palette: warm, chalky, mottled plaster —
val Chalk = Color(0xFFE7E0D2)          // base wall
val ChalkLight = Color(0xFFF2EDE2)     // floating cards
val ChalkPale = Color(0xFFF6F1E6)      // mottle highlight (brighter)
val ChalkDeep = Color(0xFFCEC1A9)      // mottle shadow (deeper)
val Sand = Color(0xFFE3DBCB)           // chips / inset surfaces
val Hairline = Color(0xFFD9D1C1)       // 0.5dp borders

val Espresso = Color(0xFF3A332A)       // primary text
val Taupe = Color(0xFF8C8270)          // muted text / labels

val Clay = Color(0xFFB0654A)           // accent
val ClayDim = Color(0xFF8F4E37)        // gradient start / pressed
val ClaySoft = Color(0xFFC98A72)       // tints
val ValueBarBg = Color(0xFFDBD3C3)     // value bar track

val MedalGold = Color(0xFFA6822F)
val MedalSilver = Color(0xFF9A958C)
val MedalBronze = Color(0xFFA0603A)

private val AlculatorColorScheme = lightColorScheme(
    primary = Clay,
    onPrimary = ChalkLight,
    primaryContainer = ClaySoft,
    onPrimaryContainer = Espresso,
    background = Chalk,
    onBackground = Espresso,
    surface = ChalkLight,
    onSurface = Espresso,
    surfaceVariant = Sand,
    onSurfaceVariant = Taupe,
    outline = Hairline,
    error = Clay,
    onError = ChalkLight,
)

/**
 * Paints the base chalk colour with soft mottled radial overlays so the
 * background reads like a hand-applied limewash wall rather than a flat fill.
 */
fun Modifier.limewashBackground(): Modifier = this
    .background(Chalk)
    // Bright cloudy highlights
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkPale, Color.Transparent),
            center = Offset(780f, 320f),
            radius = 720f
        )
    )
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkPale.copy(alpha = 0.85f), Color.Transparent),
            center = Offset(150f, 1450f),
            radius = 760f
        )
    )
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkPale.copy(alpha = 0.75f), Color.Transparent),
            center = Offset(900f, 2350f),
            radius = 780f
        )
    )
    // Deeper plaster shadows for contrast
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkDeep.copy(alpha = 0.85f), Color.Transparent),
            center = Offset(180f, 600f),
            radius = 620f
        )
    )
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkDeep.copy(alpha = 0.8f), Color.Transparent),
            center = Offset(1000f, 1150f),
            radius = 700f
        )
    )
    .background(
        Brush.radialGradient(
            colors = listOf(ChalkDeep.copy(alpha = 0.7f), Color.Transparent),
            center = Offset(420f, 1950f),
            radius = 660f
        )
    )

@Composable
fun AlculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AlculatorColorScheme,
        content = content
    )
}
