package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Nocturne VN: a cool navy and sky-blue palette, OLED-tuned. Raw palette from DESIGN.md.

// Material 3 roles
private val Surface = Color(0xFF0D131F)
private val SurfaceDim = Color(0xFF0D131F)
private val SurfaceBright = Color(0xFF333947)
private val SurfaceContainerLowest = Color(0xFF080E1A)
private val SurfaceContainerLow = Color(0xFF161C28)
private val SurfaceContainer = Color(0xFF1A202C)
private val SurfaceContainerHigh = Color(0xFF242A37)
private val SurfaceContainerHighest = Color(0xFF2F3542)
private val OnSurface = Color(0xFFDDE2F4)
private val OnSurfaceVariant = Color(0xFFBDC8D1)
private val InverseSurface = Color(0xFFDDE2F4)
private val InverseOnSurface = Color(0xFF2B303E)
private val Outline = Color(0xFF87929A)
private val OutlineVariant = Color(0xFF3E484F)
private val SurfaceTint = Color(0xFF7BD0FF)
private val Primary = Color(0xFF8ED5FF)
private val OnPrimary = Color(0xFF00354A)
private val PrimaryContainer = Color(0xFF38BDF8)
private val OnPrimaryContainer = Color(0xFF004965)
private val InversePrimary = Color(0xFF00668A)
private val Secondary = Color(0xFFFFB95F)
private val OnSecondary = Color(0xFF472A00)
private val SecondaryContainer = Color(0xFFEE9800)
private val OnSecondaryContainer = Color(0xFF5B3800)
private val Tertiary = Color(0xFFAECFFF)
private val OnTertiary = Color(0xFF00315C)
private val TertiaryContainer = Color(0xFF8AB4EC)
private val OnTertiaryContainer = Color(0xFF124577)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0D131F)
private val OnBackground = Color(0xFFDDE2F4)
private val SurfaceVariant = Color(0xFF2F3542)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val SkyBlue = Color(0xFF38BDF8)
private val SkyBluePressed = Color(0xFF0284C7)
private val Amber = Color(0xFFF59E0B)
private val VndbDeepBlue = Color(0xFF18497B)
private val SurfaceCard = Color(0xFF1A233A)
private val SurfaceElevated = Color(0xFF232F4D)
private val SurfaceInset = Color(0xFF141C2E)
private val BorderSubtle = Color(0xFF253352)
private val Emerald = Color(0xFF10B981)
private val Red = Color(0xFFEF4444)
private val Rose = Color(0xFFF43F5E)
private val Slate = Color(0xFF64748B)
private val Violet = Color(0xFFA78BFA)

// Release-type pills on Discover's Trending list:
// teal, violet and orange stay clear of the sky-blue accent, amber score and emerald notes.
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val NocturneColorScheme = darkColorScheme(
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

private val NocturneColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = SkyBlue.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = SkyBlue.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = SkyBluePressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = SkyBlue,
    accentPressed = SkyBluePressed,
    onAccent = Surface,
    accentDeep = VndbDeepBlue,
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
    statusPlaying = tinted(SkyBlue),
    statusFinished = tinted(Emerald),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(Red),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = VndbDeepBlue,
    chipActiveContent = SkyBlue,
)

/** Nocturne VN. Shared by every theme that keeps the original look. */
internal val NocturneScheme = VndbScheme(
    colorScheme = NocturneColorScheme,
    colors = NocturneColors,
)
