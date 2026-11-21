package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Neon: a retro phosphor terminal. Neon green on pure black, green-tinted text and dim green
// borders, with arcade cyan, magenta and yellow for the secondary signals.

// Material 3 roles
private val Surface = Color(0xFF000000)
private val SurfaceDim = Color(0xFF000000)
private val SurfaceBright = Color(0xFF1F2A1F)
private val SurfaceContainerLowest = Color(0xFF000000)
private val SurfaceContainerLow = Color(0xFF050A05)
private val SurfaceContainer = Color(0xFF0A120A)
private val SurfaceContainerHigh = Color(0xFF101C10)
private val SurfaceContainerHighest = Color(0xFF172617)
private val OnSurface = Color(0xFFC8FFC0)
private val OnSurfaceVariant = Color(0xFF7FD27A)
private val InverseSurface = Color(0xFFC8FFC0)
private val InverseOnSurface = Color(0xFF0A120A)
private val Outline = Color(0xFF3FA03A)
private val OutlineVariant = Color(0xFF1E4A1C)
private val SurfaceTint = Color(0xFF39FF14)
private val Primary = Color(0xFF39FF14)
private val OnPrimary = Color(0xFF000000)
private val PrimaryContainer = Color(0xFF2BD10F)
private val OnPrimaryContainer = Color(0xFF001A00)
private val InversePrimary = Color(0xFF157A00)
private val Secondary = Color(0xFFFF2BD6)
private val OnSecondary = Color(0xFF000000)
private val SecondaryContainer = Color(0xFFB0128F)
private val OnSecondaryContainer = Color(0xFFFFE0F7)
private val Tertiary = Color(0xFF00F0FF)
private val OnTertiary = Color(0xFF000000)
private val TertiaryContainer = Color(0xFF00A3AD)
private val OnTertiaryContainer = Color(0xFF001F21)
private val Error = Color(0xFFFF5A5A)
private val OnError = Color(0xFF000000)
private val ErrorContainer = Color(0xFF5C0000)
private val OnErrorContainer = Color(0xFFFFD6D6)
private val Background = Color(0xFF000000)
private val OnBackground = Color(0xFFC8FFC0)
private val SurfaceVariant = Color(0xFF172617)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val NeonGreen = Color(0xFF39FF14)
private val NeonGreenPressed = Color(0xFF2BD10F)
private val NeonGreenDeep = Color(0xFF0B3D05)
private val NeonYellow = Color(0xFFFFE600)
private val NeonCyan = Color(0xFF00F0FF)
private val NeonMagenta = Color(0xFFFF2BD6)
private val NeonRed = Color(0xFFFF3B3B)
private val SurfaceCard = Color(0xFF071107)
private val SurfaceElevated = Color(0xFF0E1C0E)
private val SurfaceInset = Color(0xFF040A04)
private val BorderSubtle = Color(0xFF1E4A1C)
private val DimGreen = Color(0xFF4E8A4A)

// Release-type pills on Discover's Trending list:
// neon cyan, violet and orange beside the neon green accent and yellow score.
private val ReleaseNewColor = Color(0xFF00F0FF)
private val ReleaseTranslationColor = Color(0xFFB967FF)
private val ReleasePatchColor = Color(0xFFFF9F1C)

private val NeonColorScheme = darkColorScheme(
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

private val NeonColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = NeonGreen.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = NeonGreen.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = NeonGreen.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = NeonGreen.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = NeonGreen,
    accentPressed = NeonGreenPressed,
    onAccent = Surface,
    accentDeep = NeonGreenDeep,
    score = NeonYellow,
    placeholder = DimGreen,
    spoilerNone = tinted(NeonGreen),
    spoilerMinor = tinted(NeonYellow),
    spoilerMajor = tintedStrong(NeonRed),
    tagNsfw = tinted(NeonMagenta),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(ReleaseNewColor),
    releaseTranslation = tinted(ReleaseTranslationColor),
    releasePatch = tinted(ReleasePatchColor),
    statusPlaying = tinted(NeonCyan),
    statusFinished = tinted(NeonGreen),
    statusStalled = tinted(NeonYellow),
    statusDropped = tintedStrong(NeonRed),
    labelWishlist = TintedColors(Color.Transparent, NeonMagenta, NeonMagenta),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, DimGreen),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = NeonGreenDeep,
    chipActiveContent = NeonGreen,
)

/** Neon: retro-terminal neon green on black. */
internal val NeonScheme = VndbScheme(
    colorScheme = NeonColorScheme,
    colors = NeonColors,
)
