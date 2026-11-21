package com.zc.vndb.common.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Raw palettes live in `schemes/`, one file per color scheme. Screens never read them directly:
// use MaterialTheme.colorScheme or VndbTheme.colors.

/** A pill/badge treatment: tinted fill, text and hairline border. */
@Immutable
data class TintedColors(val container: Color, val content: Color, val border: Color)

/** A [TintedColors] derived from one [base] color. */
internal fun tinted(base: Color, containerAlpha: Float = 0.12f, borderAlpha: Float = 0.25f) =
    TintedColors(
        container = base.copy(alpha = containerAlpha),
        content = base,
        border = base.copy(alpha = borderAlpha),
    )

/** The stronger tint used for warnings: spoiler "major" and a dropped game. */
internal fun tintedStrong(base: Color) = tinted(base, containerAlpha = 0.15f, borderAlpha = 0.35f)

/** Opacities shared by every scheme, so the same token looks the same in each. */
internal object TokenAlpha {
    const val GLASS = 0.85f
    const val HAIRLINE = 0.08f
    const val FOCUS_BORDER = 0.35f
    const val FOCUS_RING = 0.2f
    const val GLOW = 0.15f
}

/**
 * Colors from DESIGN.md that have no Material 3 role. Read through [VndbTheme.colors].
 * Every color scheme sets all of them: there are no defaults, so a new scheme cannot silently
 * inherit another scheme's colors.
 */
@Immutable
data class VndbColors(
    // Surfaces (elevation levels 1–3)
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val surfaceInset: Color,
    val surfaceGlass: Color,
    val glassHairline: Color,
    val borderSubtle: Color,
    val borderFocused: Color,
    val focusRing: Color,
    val glow: Color,
    val coverScrim: Color,

    // Brand accents
    val accent: Color,
    val accentPressed: Color,
    val onAccent: Color,
    /** A darker shade of the accent, behind accent-colored content (e.g. the active filter chip). */
    val accentDeep: Color,
    val score: Color,
    val placeholder: Color,

    // Spoiler tiers & content flags
    val spoilerNone: TintedColors,
    val spoilerMinor: TintedColors,
    val spoilerMajor: TintedColors,
    val tagNsfw: TintedColors,

    // Neutral metadata chips (tags, platforms, length); tappable chips use the accent instead
    val chipNeutral: TintedColors,

    // Release-type pills on Discover's Trending list; a re-release uses chipNeutral
    val releaseNew: TintedColors,
    val releaseTranslation: TintedColors,
    val releasePatch: TintedColors,

    // Tracking status badges
    val statusPlaying: TintedColors,
    val statusFinished: TintedColors,
    val statusStalled: TintedColors,
    val statusDropped: TintedColors,

    // List labels: outline only, so they stay quieter than the status
    val labelWishlist: TintedColors,
    val labelBlacklist: TintedColors,
    val labelOwn: TintedColors,

    // Filter chips
    val chipInactiveContainer: Color,
    val chipActiveContainer: Color,
    val chipActiveContent: Color,
)

internal val LocalVndbColors = staticCompositionLocalOf { OriginalThemeSpec.colors }
