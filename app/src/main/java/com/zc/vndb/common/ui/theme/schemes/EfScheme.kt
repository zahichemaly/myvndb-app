package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// ef - a fairy tale of the two.: twilight mauve with an apricot accent and a lilac secondary. The patch pill is
// pink, clear of the apricot.
// Anchors: background #15131D, cards #221E2D, text #F3EEF6, accent #FFB088, secondary #B8A4E3, score #F5D17A.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF15131D)
private val SurfaceDim = Color(0xFF15131D)
private val SurfaceBright = Color(0xFF433F4D)
private val SurfaceContainerLowest = Color(0xFF0E0C13)
private val SurfaceContainerLow = Color(0xFF1B1824)
private val SurfaceContainer = Color(0xFF221E2D)
private val SurfaceContainerHigh = Color(0xFF2C2837)
private val SurfaceContainerHighest = Color(0xFF373341)
private val OnSurface = Color(0xFFF3EEF6)
private val OnSurfaceVariant = Color(0xFFBCB7C0)
private val InverseSurface = Color(0xFFF3EEF6)
private val InverseOnSurface = Color(0xFF2A2635)
private val Outline = Color(0xFF8F8B94)
private val OutlineVariant = Color(0xFF3D3947)
private val SurfaceTint = Color(0xFFFFC8AC)
private val Primary = Color(0xFFFFD4BE)
private val OnPrimary = Color(0xFF4D3529)
private val PrimaryContainer = Color(0xFFFFB088)
private val OnPrimaryContainer = Color(0xFF0D0B11)
private val InversePrimary = Color(0xFFCC8D6D)
private val Secondary = Color(0xFFD1C4ED)
private val OnSecondary = Color(0xFF342E40)
private val SecondaryContainer = Color(0xFF5C5272)
private val OnSecondaryContainer = Color(0xFFF1EDF9)
private val Tertiary = Color(0xFFF8DC9B)
private val OnTertiary = Color(0xFF453B22)
private val TertiaryContainer = Color(0xFF675833)
private val OnTertiaryContainer = Color(0xFFFEF8EB)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF15131D)
private val OnBackground = Color(0xFFF3EEF6)
private val SurfaceVariant = Color(0xFF373341)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFFFB088)
private val AccentPressed = Color(0xFFC7896A)
private val AccentDeep = Color(0xFF573F3B)
private val AccentTint = Color(0xFFFFCCB2)
private val OnAccent = Color(0xFF0D0B11)
private val Score = Color(0xFFF5D17A)
private val Placeholder = Color(0xFF84808A)
private val SurfaceCard = Color(0xFF221E2D)
private val SurfaceElevated = Color(0xFF312D3B)
private val SurfaceInset = Color(0xFF1A1723)
private val BorderSubtle = Color(0xFF35313F)
private val PlayingColor = Color(0xFF9CC7F0)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFFF7A93)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB8A4E3)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFF9A8D4)

private val EfColorScheme = darkColorScheme(
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

private val EfColors = VndbColors(
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

/** Twilight-mauve surfaces, pale lilac text and an apricot accent. */
internal val EfScheme = VndbScheme(
    colorScheme = EfColorScheme,
    colors = EfColors,
)
