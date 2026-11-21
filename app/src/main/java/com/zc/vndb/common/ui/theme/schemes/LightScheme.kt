package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Light: Nocturne VN's sky blue and amber on a cool off-white. Accents are darker shades of the
// Nocturne ones so they keep >= 4.5:1 on white cards.

// Material 3 roles
private val Surface = Color(0xFFF6F8FC)
private val SurfaceDim = Color(0xFFDCE1EA)
private val SurfaceBright = Color(0xFFFFFFFF)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val SurfaceContainerLow = Color(0xFFF0F3F9)
private val SurfaceContainer = Color(0xFFEAEEF5)
private val SurfaceContainerHigh = Color(0xFFE3E8F0)
private val SurfaceContainerHighest = Color(0xFFDCE2EB)
private val OnSurface = Color(0xFF0F172A)
private val OnSurfaceVariant = Color(0xFF475569)
private val InverseSurface = Color(0xFF1E293B)
private val InverseOnSurface = Color(0xFFEEF2F8)
private val Outline = Color(0xFF64748B)
private val OutlineVariant = Color(0xFFCBD5E1)
private val SurfaceTint = Color(0xFF0369A1)
private val Primary = Color(0xFF0369A1)
private val OnPrimary = Color(0xFFFFFFFF)
private val PrimaryContainer = Color(0xFFCFE8FA)
private val OnPrimaryContainer = Color(0xFF003A57)
private val InversePrimary = Color(0xFF7DD3FC)
private val Secondary = Color(0xFFB45309)
private val OnSecondary = Color(0xFFFFFFFF)
private val SecondaryContainer = Color(0xFFFDE6C4)
private val OnSecondaryContainer = Color(0xFF4A2500)
private val Tertiary = Color(0xFF1E4F85)
private val OnTertiary = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFFD6E4F7)
private val OnTertiaryContainer = Color(0xFF0E2D52)
private val Error = Color(0xFFBA1A1A)
private val OnError = Color(0xFFFFFFFF)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF410002)
private val Background = Color(0xFFF6F8FC)
private val OnBackground = Color(0xFF0F172A)
private val SurfaceVariant = Color(0xFFE2E8F0)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Sky = Color(0xFF0369A1)
private val SkyPressed = Color(0xFF075985)
private val SkyTint = Color(0xFFDCEEFB)
private val Amber = Color(0xFFB45309)
private val SurfaceCard = Color(0xFFFFFFFF)

// On light, "elevated" is darker than the recessed "inset" grey, so the selected-tab pill
// (elevated on the inset nav bar) stays visible.
private val SurfaceElevated = Color(0xFFD6DEE9)
private val SurfaceInset = Color(0xFFE9EEF5)
private val BorderSubtle = Color(0xFFD5DDE8)
private val Emerald = Color(0xFF047857)
private val Red = Color(0xFFDC2626)
private val Rose = Color(0xFFE11D48)
private val Slate = Color(0xFF64748B)
private val Violet = Color(0xFF6D28D9)

// Release-type pills on Discover's Trending list:
// dark enough to read on light cards; clear of the sky accent and amber score.
private val ReleaseNewColor = Color(0xFF0F766E)
private val ReleaseTranslationColor = Color(0xFF6D28D9)
private val ReleasePatchColor = Color(0xFFC2410C)

private val LightColorScheme = lightColorScheme(
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

private val LightColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceCard.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.Black.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Sky.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Sky.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = Sky.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Sky,
    accentPressed = SkyPressed,
    onAccent = OnPrimary,
    accentDeep = SkyTint,
    score = Amber,
    placeholder = Slate,
    spoilerNone = tinted(Emerald),
    spoilerMinor = tinted(Amber),
    spoilerMajor = tintedStrong(Red),
    tagNsfw = tinted(Rose),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(ReleaseNewColor),
    releaseTranslation = tinted(ReleaseTranslationColor),
    releasePatch = tinted(ReleasePatchColor),
    statusPlaying = tinted(Sky),
    statusFinished = tinted(Emerald),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(Red),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = SkyTint,
    chipActiveContent = Sky,
)

/** Light: dark text on a cool off-white, with the Nocturne accents darkened to read on white. */
internal val LightScheme = VndbScheme(
    colorScheme = LightColorScheme,
    colors = LightColors,
)
