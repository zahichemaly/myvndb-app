package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Steins;Gate: an olive-black lab with the amber of the Nixie-tube divergence meter as accent, Kurisu's auburn as
// secondary and an IBN-gold score. "Playing" is a cool steel blue and the patch pill is pink, to stay clear of the
// orange accent.
// Anchors: background #12110D, cards #1F1D16, text #EDE7D6, accent #FF8A1F, secondary #D0654C, score #E9C46A.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF12110D)
private val SurfaceDim = Color(0xFF12110D)
private val SurfaceBright = Color(0xFF403D35)
private val SurfaceContainerLowest = Color(0xFF0C0B08)
private val SurfaceContainerLow = Color(0xFF181611)
private val SurfaceContainer = Color(0xFF1F1D16)
private val SurfaceContainerHigh = Color(0xFF292720)
private val SurfaceContainerHighest = Color(0xFF343129)
private val OnSurface = Color(0xFFEDE7D6)
private val OnSurfaceVariant = Color(0xFFB6B2A4)
private val InverseSurface = Color(0xFFEDE7D6)
private val InverseOnSurface = Color(0xFF27251E)
private val Outline = Color(0xFF8A877C)
private val OutlineVariant = Color(0xFF3A372F)
private val SurfaceTint = Color(0xFFFFAD62)
private val Primary = Color(0xFFFFBF84)
private val OnPrimary = Color(0xFF4D2909)
private val PrimaryContainer = Color(0xFFFF8A1F)
private val OnPrimaryContainer = Color(0xFF0B0A08)
private val InversePrimary = Color(0xFFCC6E19)
private val Secondary = Color(0xFFE09B8B)
private val OnSecondary = Color(0xFF3A1C15)
private val SecondaryContainer = Color(0xFF683226)
private val OnSecondaryContainer = Color(0xFFF6E0DB)
private val Tertiary = Color(0xFFEED38F)
private val OnTertiary = Color(0xFF41371E)
private val TertiaryContainer = Color(0xFF62522D)
private val OnTertiaryContainer = Color(0xFFFCF6E9)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF12110D)
private val OnBackground = Color(0xFFEDE7D6)
private val SurfaceVariant = Color(0xFF343129)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFFF8A1F)
private val AccentPressed = Color(0xFFC76C18)
private val AccentDeep = Color(0xFF543312)
private val AccentTint = Color(0xFFFFB36D)
private val OnAccent = Color(0xFF0B0A08)
private val Score = Color(0xFFE9C46A)
private val Placeholder = Color(0xFF807C72)
private val SurfaceCard = Color(0xFF1F1D16)
private val SurfaceElevated = Color(0xFF2D2B23)
private val SurfaceInset = Color(0xFF171510)
private val BorderSubtle = Color(0xFF322F27)
private val PlayingColor = Color(0xFF7FB3E8)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFE9C46A)
private val DroppedColor = Color(0xFFF0617F)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFF472B6)

private val SteinsGateColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = SurfaceTint,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = Scrim,
    surfaceBright = SurfaceBright,
    surfaceDim = SurfaceDim,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
)

private val SteinsGateColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Accent.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Accent.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = AccentPressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Accent,
    accentPressed = AccentPressed,
    onAccent = OnAccent,
    accentDeep = AccentDeep,
    score = Score,
    placeholder = Placeholder,
    spoilerNone = tinted(FinishedColor),
    spoilerMinor = tinted(StalledColor),
    spoilerMajor = tintedStrong(DroppedColor),
    tagNsfw = tinted(NsfwColor),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(ReleaseNewColor),
    releaseTranslation = tinted(ReleaseTranslationColor),
    releasePatch = tinted(ReleasePatchColor),
    statusPlaying = tinted(PlayingColor),
    statusFinished = tinted(FinishedColor),
    statusStalled = tinted(StalledColor),
    statusDropped = tintedStrong(DroppedColor),
    labelWishlist = TintedColors(Color.Transparent, WishlistColor, WishlistColor),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Placeholder),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = AccentDeep,
    chipActiveContent = AccentTint,
)

/** Olive-black surfaces, parchment text and a Nixie-amber accent. */
internal val SteinsGateScheme = VndbScheme(
    colorScheme = SteinsGateColorScheme,
    colors = SteinsGateColors,
)
