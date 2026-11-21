package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Clannad: blush white with a sakura-rose accent and a dango-amber secondary and score. Accents are dark enough for
// >= 4.5:1 on white cards.
// Anchors: background #FFF5F7, cards #FFFFFF, text #2E1F26, accent #C2416B, secondary #A35F14, score #A35F14.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFFFFF5F7)
private val SurfaceDim = Color(0xFFE8DDE0)
private val SurfaceBright = Color(0xFFFFFFFF)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val SurfaceContainerLow = Color(0xFFFAF0F2)
private val SurfaceContainer = Color(0xFFF5EAED)
private val SurfaceContainerHigh = Color(0xFFEEE4E6)
private val SurfaceContainerHighest = Color(0xFFE8DDE0)
private val OnSurface = Color(0xFF2E1F26)
private val OnSurfaceVariant = Color(0xFF6D5F65)
private val InverseSurface = Color(0xFF3F3037)
private val InverseOnSurface = Color(0xFFFFFAFB)
private val Outline = Color(0xFF968A8E)
private val OutlineVariant = Color(0xFFDBD1D3)
private val SurfaceTint = Color(0xFFC2416B)
private val Primary = Color(0xFFC2416B)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFF4DDE4)
private val OnPrimaryContainer = Color(0xFF571D30)
private val InversePrimary = Color(0xFFE0A0B5)
private val Secondary = Color(0xFFA35F14)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFEDDFD0)
private val OnSecondaryContainer = Color(0xFF492B09)
private val Tertiary = Color(0xFFA35F14)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFEDDFD0)
private val OnTertiaryContainer = Color(0xFF492B09)
private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)
private val Background = Color(0xFFFFF5F7)
private val OnBackground = Color(0xFF2E1F26)
private val SurfaceVariant = Color(0xFFEDE3E5)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFC2416B)
private val AccentPressed = Color(0xFF9B3456)
private val AccentDeep = Color(0xFFF6E4EA)
private val OnAccent = Color(0xFFFFFFFF)
private val Score = Color(0xFFA35F14)
private val Placeholder = Color(0xFF968A8E)
private val SurfaceCard = Color(0xFFFFFFFF)

// On light, "elevated" is darker than the recessed "inset", so the selected-tab pill
// (elevated on the inset nav bar) stays visible.
private val SurfaceElevated = Color(0xFFEAE0E2)
private val SurfaceInset = Color(0xFFF7ECEF)
private val BorderSubtle = Color(0xFFEAE0E2)
private val PlayingColor = Color(0xFF2F5F9E)
private val FinishedColor = Color(0xFF046C4E)
private val StalledColor = Color(0xFF8A4F0F)
private val DroppedColor = Color(0xFFB91C1C)
private val NsfwColor = Color(0xFF9D174D)
private val WishlistColor = Color(0xFF5B21B6)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF0E6660)
private val ReleaseTranslationColor = Color(0xFF5B21B6)
private val ReleasePatchColor = Color(0xFF9A3412)

private val ClannadColorScheme = lightColorScheme(
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

private val ClannadColors = VndbColors(
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

/** Clannad: blush-white surfaces, plum-brown text and a sakura-rose accent. */
internal val ClannadScheme = VndbScheme(
    colorScheme = ClannadColorScheme,
    colors = ClannadColors,
)
