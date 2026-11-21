package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Kara no Shoujo: a 1950s black-and-white photograph. A bone-white accent, with blood red only for the secondary,
// "dropped" and major spoilers.
// Anchors: background #121110, cards #1E1C1A, text #E9E5DE, accent #DCD5C8, secondary #E35B62, score #D9B26A.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF121110)
private val SurfaceDim = Color(0xFF121110)
private val SurfaceBright = Color(0xFF3E3C39)
private val SurfaceContainerLowest = Color(0xFF0C0B0A)
private val SurfaceContainerLow = Color(0xFF171614)
private val SurfaceContainer = Color(0xFF1E1C1A)
private val SurfaceContainerHigh = Color(0xFF282624)
private val SurfaceContainerHighest = Color(0xFF32302E)
private val OnSurface = Color(0xFFE9E5DE)
private val OnSurfaceVariant = Color(0xFFB3B0AA)
private val InverseSurface = Color(0xFFE9E5DE)
private val InverseOnSurface = Color(0xFF262422)
private val Outline = Color(0xFF888681)
private val OutlineVariant = Color(0xFF383633)
private val SurfaceTint = Color(0xFFE6E2D8)
private val Primary = Color(0xFFECE8E1)
private val OnPrimary = Color(0xFF42403C)
private val PrimaryContainer = Color(0xFFDCD5C8)
private val OnPrimaryContainer = Color(0xFF0B0A0A)
private val InversePrimary = Color(0xFFB0AAA0)
private val Secondary = Color(0xFFED9499)
private val OnSecondary = Color(0xFF40191B)
private val SecondaryContainer = Color(0xFF722E31)
private val OnSecondaryContainer = Color(0xFFF9DEE0)
private val Tertiary = Color(0xFFE2C58F)
private val OnTertiary = Color(0xFF3D321E)
private val TertiaryContainer = Color(0xFF5B4B2D)
private val OnTertiaryContainer = Color(0xFFF9F3E9)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF121110)
private val OnBackground = Color(0xFFE9E5DE)
private val SurfaceVariant = Color(0xFF32302E)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFDCD5C8)
private val AccentPressed = Color(0xFFACA69C)
private val AccentDeep = Color(0xFF4B4844)
private val AccentTint = Color(0xFFE8E4DB)
private val OnAccent = Color(0xFF0B0A0A)
private val Score = Color(0xFFD9B26A)
private val Placeholder = Color(0xFF7E7B77)
private val SurfaceCard = Color(0xFF1E1C1A)
private val SurfaceElevated = Color(0xFF2C2A28)
private val SurfaceInset = Color(0xFF161514)
private val BorderSubtle = Color(0xFF302E2C)
private val PlayingColor = Color(0xFFA9B8C9)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFEC7076)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val KaraNoShoujoColorScheme = darkColorScheme(
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

private val KaraNoShoujoColors = VndbColors(
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

/** Ash-black surfaces, bone text and a bone-white accent; red only for danger. */
internal val KaraNoShoujoScheme = VndbScheme(
    colorScheme = KaraNoShoujoColorScheme,
    colors = KaraNoShoujoColors,
)
