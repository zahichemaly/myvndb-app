package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Dark: neutral charcoal greys with no blue cast (unlike Nocturne's navy), a soft periwinkle
// accent and a warm gold for scores.

// Material 3 roles
private val Surface = Color(0xFF121212)
private val SurfaceDim = Color(0xFF121212)
private val SurfaceBright = Color(0xFF383838)
private val SurfaceContainerLowest = Color(0xFF0B0B0B)
private val SurfaceContainerLow = Color(0xFF1A1A1A)
private val SurfaceContainer = Color(0xFF1E1E1E)
private val SurfaceContainerHigh = Color(0xFF282828)
private val SurfaceContainerHighest = Color(0xFF333333)
private val OnSurface = Color(0xFFE6E6E6)
private val OnSurfaceVariant = Color(0xFFB3B3B3)
private val InverseSurface = Color(0xFFE6E6E6)
private val InverseOnSurface = Color(0xFF2A2A2A)
private val Outline = Color(0xFF8A8A8A)
private val OutlineVariant = Color(0xFF444444)
private val SurfaceTint = Color(0xFFA8C7FA)
private val Primary = Color(0xFFA8C7FA)
private val OnPrimary = Color(0xFF0A305F)
private val PrimaryContainer = Color(0xFF8AB4F8)
private val OnPrimaryContainer = Color(0xFF0A2A55)
private val InversePrimary = Color(0xFF3A6BC4)
private val Secondary = Color(0xFFF2C46B)
private val OnSecondary = Color(0xFF3F2D00)
private val SecondaryContainer = Color(0xFFC99A2E)
private val OnSecondaryContainer = Color(0xFF2E2000)
private val Tertiary = Color(0xFFC3C7CF)
private val OnTertiary = Color(0xFF2C3036)
private val TertiaryContainer = Color(0xFF8D9199)
private val OnTertiaryContainer = Color(0xFF1F2328)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF121212)
private val OnBackground = Color(0xFFE6E6E6)
private val SurfaceVariant = Color(0xFF333333)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Periwinkle = Color(0xFF8AB4F8)
private val PeriwinklePressed = Color(0xFF5E8FE0)
private val PeriwinkleDeep = Color(0xFF263A5C)
private val Gold = Color(0xFFF2C46B)
private val SurfaceCard = Color(0xFF1E1E1E)
private val SurfaceElevated = Color(0xFF2A2A2A)
private val SurfaceInset = Color(0xFF171717)
private val BorderSubtle = Color(0xFF333333)
private val Emerald = Color(0xFF10B981)
private val Amber = Color(0xFFF59E0B)
private val Red = Color(0xFFF87171)
private val Rose = Color(0xFFF43F5E)
private val Grey = Color(0xFF757575)
private val Violet = Color(0xFFA78BFA)

// Release-type pills on Discover's Trending list:
// teal, violet and orange stay clear of the periwinkle accent, gold score and emerald notes.
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val DarkColorScheme = darkColorScheme(
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

private val DarkColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Periwinkle.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Periwinkle.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = PeriwinklePressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Periwinkle,
    accentPressed = PeriwinklePressed,
    onAccent = Surface,
    accentDeep = PeriwinkleDeep,
    score = Gold,
    placeholder = Grey,
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
    statusPlaying = tinted(Periwinkle),
    statusFinished = tinted(Emerald),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(Red),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Grey),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = PeriwinkleDeep,
    chipActiveContent = Periwinkle,
)

/** Dark: neutral charcoal greys with a periwinkle accent. */
internal val DarkScheme = VndbScheme(
    colorScheme = DarkColorScheme,
    colors = DarkColors,
)
