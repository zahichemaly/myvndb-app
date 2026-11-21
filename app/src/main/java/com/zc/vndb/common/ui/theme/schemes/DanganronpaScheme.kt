package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Danganronpa: Monokuma's black and white with the series' pink blood as accent. "Playing" is plain white-grey and
// NSFW moves to lavender, clear of the pink.
// Anchors: background #0A0A0A, cards #181818, text #F5F5F5, accent #FF2E8B, secondary #D9D9D9, score #FFD34D.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF0A0A0A)
private val SurfaceDim = Color(0xFF0A0A0A)
private val SurfaceBright = Color(0xFF3B3B3B)
private val SurfaceContainerLowest = Color(0xFF060606)
private val SurfaceContainerLow = Color(0xFF101010)
private val SurfaceContainer = Color(0xFF181818)
private val SurfaceContainerHigh = Color(0xFF232323)
private val SurfaceContainerHighest = Color(0xFF2E2E2E)
private val OnSurface = Color(0xFFF5F5F5)
private val OnSurfaceVariant = Color(0xFFBABABA)
private val InverseSurface = Color(0xFFF5F5F5)
private val InverseOnSurface = Color(0xFF212121)
private val Outline = Color(0xFF8B8B8B)
private val OutlineVariant = Color(0xFF353535)
private val SurfaceTint = Color(0xFFFF6DAE)
private val Primary = Color(0xFFFF8CBF)
private val OnPrimary = Color(0xFF4D0E2A)
private val PrimaryContainer = Color(0xFFFF2E8B)
private val OnPrimaryContainer = Color(0xFF060606)
private val InversePrimary = Color(0xFFCC256F)
private val Secondary = Color(0xFFE6E6E6)
private val OnSecondary = Color(0xFF3D3D3D)
private val SecondaryContainer = Color(0xFF6C6C6C)
private val OnSecondaryContainer = Color(0xFFF7F7F7)
private val Tertiary = Color(0xFFFFDE7A)
private val OnTertiary = Color(0xFF473B16)
private val TertiaryContainer = Color(0xFF6B5920)
private val OnTertiaryContainer = Color(0xFFFFF8E4)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0A0A0A)
private val OnBackground = Color(0xFFF5F5F5)
private val SurfaceVariant = Color(0xFF2E2E2E)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFFF2E8B)
private val AccentPressed = Color(0xFFC7246C)
private val AccentDeep = Color(0xFF4F142E)
private val AccentTint = Color(0xFFFF77B4)
private val OnAccent = Color(0xFF060606)
private val Score = Color(0xFFFFD34D)
private val Placeholder = Color(0xFF808080)
private val SurfaceCard = Color(0xFF181818)
private val SurfaceElevated = Color(0xFF272727)
private val SurfaceInset = Color(0xFF0F0F0F)
private val BorderSubtle = Color(0xFF2C2C2C)
private val PlayingColor = Color(0xFFD9D9D9)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFFF6B6B)
private val NsfwColor = Color(0xFFC4B5FD)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val DanganronpaColorScheme = darkColorScheme(
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

private val DanganronpaColors = VndbColors(
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

/** Black surfaces, white text and a shocking-pink accent. */
internal val DanganronpaScheme = VndbScheme(
    colorScheme = DanganronpaColorScheme,
    colors = DanganronpaColors,
)
