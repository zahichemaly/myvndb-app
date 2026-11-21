package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Muv-Luv Alternative: a gunmetal hangar with a hazard-yellow accent and a BETA-red secondary. The score shares the
// accent yellow, so "stalled" moves to light orange.
// Anchors: background #0E1013, cards #1A1D22, text #E3E6EA, accent #F5C518, secondary #E5604C, score #F5C518.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF0E1013)
private val SurfaceDim = Color(0xFF0E1013)
private val SurfaceBright = Color(0xFF3A3D42)
private val SurfaceContainerLowest = Color(0xFF090A0C)
private val SurfaceContainerLow = Color(0xFF13161A)
private val SurfaceContainer = Color(0xFF1A1D22)
private val SurfaceContainerHigh = Color(0xFF24272C)
private val SurfaceContainerHighest = Color(0xFF2E3136)
private val OnSurface = Color(0xFFE3E6EA)
private val OnSurfaceVariant = Color(0xFFAEB0B4)
private val InverseSurface = Color(0xFFE3E6EA)
private val InverseOnSurface = Color(0xFF22252A)
private val Outline = Color(0xFF838689)
private val OutlineVariant = Color(0xFF34373C)
private val SurfaceTint = Color(0xFFF8D65D)
private val Primary = Color(0xFFFADF80)
private val OnPrimary = Color(0xFF4A3B07)
private val PrimaryContainer = Color(0xFFF5C518)
private val OnPrimaryContainer = Color(0xFF080A0B)
private val InversePrimary = Color(0xFFC49E13)
private val Secondary = Color(0xFFEE988B)
private val OnSecondary = Color(0xFF401B15)
private val SecondaryContainer = Color(0xFF723026)
private val OnSecondaryContainer = Color(0xFFFADFDB)
private val Tertiary = Color(0xFFF8D452)
private val OnTertiary = Color(0xFF453707)
private val TertiaryContainer = Color(0xFF67530A)
private val OnTertiaryContainer = Color(0xFFFEF6DC)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0E1013)
private val OnBackground = Color(0xFFE3E6EA)
private val SurfaceVariant = Color(0xFF2E3136)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFF5C518)
private val AccentPressed = Color(0xFFBF9A13)
private val AccentDeep = Color(0xFF4F4314)
private val AccentTint = Color(0xFFF8D969)
private val OnAccent = Color(0xFF080A0B)
private val Score = Color(0xFFF5C518)
private val Placeholder = Color(0xFF787B7E)
private val SurfaceCard = Color(0xFF1A1D22)
private val SurfaceElevated = Color(0xFF282B30)
private val SurfaceInset = Color(0xFF121518)
private val BorderSubtle = Color(0xFF2C2F34)
private val PlayingColor = Color(0xFF7FA7D9)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFFDBA74)
private val DroppedColor = Color(0xFFF0617F)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFF472B6)

private val MuvLuvAlternativeColorScheme = darkColorScheme(
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

private val MuvLuvAlternativeColors = VndbColors(
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

/** Gunmetal surfaces, cool white text and a hazard-yellow accent. */
internal val MuvLuvAlternativeScheme = VndbScheme(
    colorScheme = MuvLuvAlternativeColorScheme,
    colors = MuvLuvAlternativeColors,
)
