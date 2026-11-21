package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Sepia: warm paper and ink for long reading sessions. A burnt-sienna accent, olive secondary and old-gold score.
// The release-patch pill is plum, since orange would sit next to the sienna accent.
// Anchors: background #F4ECDD, cards #FBF6EC, text #3A2E25, accent #9C4A22, secondary #5E6B2E, score #8A6510.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFFF4ECDD)
private val SurfaceDim = Color(0xFFE0D7C9)
private val SurfaceBright = Color(0xFFFBF6EC)
private val SurfaceContainerLowest = Color(0xFFFBF6EC)
private val SurfaceContainerLow = Color(0xFFEFE7D8)
private val SurfaceContainer = Color(0xFFEBE2D4)
private val SurfaceContainerHigh = Color(0xFFE5DDCE)
private val SurfaceContainerHighest = Color(0xFFE0D7C9)
private val OnSurface = Color(0xFF3A2E25)
private val OnSurfaceVariant = Color(0xFF72675C)
private val InverseSurface = Color(0xFF493D34)
private val InverseOnSurface = Color(0xFFFAF6EE)
private val Outline = Color(0xFF978D81)
private val OutlineVariant = Color(0xFFD4CCBE)
private val SurfaceTint = Color(0xFF9C4A22)
private val Primary = Color(0xFF9C4A22)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFEDDED7)
private val OnPrimaryContainer = Color(0xFF46210F)
private val InversePrimary = Color(0xFFCEA490)
private val Secondary = Color(0xFF5E6B2E)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFDFE1D5)
private val OnSecondaryContainer = Color(0xFF2A3015)
private val Tertiary = Color(0xFF8A6510)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFE8E0CF)
private val OnTertiaryContainer = Color(0xFF3E2D07)
private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)
private val Background = Color(0xFFF4ECDD)
private val OnBackground = Color(0xFF3A2E25)
private val SurfaceVariant = Color(0xFFE4DCCD)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFF9C4A22)
private val AccentPressed = Color(0xFF7D3B1B)
private val AccentDeep = Color(0xFFF1E6E0)
private val OnAccent = Color(0xFFFFFFFF)
private val Score = Color(0xFF8A6510)
private val Placeholder = Color(0xFF978D81)
private val SurfaceCard = Color(0xFFFBF6EC)

// On light, "elevated" is darker than the recessed "inset", so the selected-tab pill
// (elevated on the inset nav bar) stays visible.
private val SurfaceElevated = Color(0xFFE1D9CB)
private val SurfaceInset = Color(0xFFEDE4D6)
private val BorderSubtle = Color(0xFFE1D9CB)
private val PlayingColor = Color(0xFF2A527A)
private val FinishedColor = Color(0xFF045C42)
private val StalledColor = Color(0xFF74550C)
private val DroppedColor = Color(0xFFA01818)
private val NsfwColor = Color(0xFF9F1239)
private val WishlistColor = Color(0xFF5B21B6)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF0B5A55)
private val ReleaseTranslationColor = Color(0xFF5B21B6)
private val ReleasePatchColor = Color(0xFF9D174D)

private val SepiaColorScheme = lightColorScheme(
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

private val SepiaColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceCard.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.Black.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Accent.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Accent.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = Accent.copy(alpha = TokenAlpha.GLOW),
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
    chipActiveContent = AccentPressed,
)

/** Sepia: warm parchment surfaces, ink-brown text and a burnt-sienna accent. */
internal val SepiaScheme = VndbScheme(
    colorScheme = SepiaColorScheme,
    colors = SepiaColors,
)
