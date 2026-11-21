package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Saya no Uta: a deep wine palette with a teal accent, taken from the wallpaper's colors:
// crimson background (#833F3E, darkened for the surfaces), teal hair (#2F7272, lightened for the
// accent so it reads on dark), cream dress (#F3ECE4, the text color), slate-blue shadows (#69848E)
// and the olive-gold of the lower corner (#8B7B3A, lightened for the score).

// Material 3 roles
private val Surface = Color(0xFF180E10)
private val SurfaceDim = Color(0xFF180E10)
private val SurfaceBright = Color(0xFF3C2A2D)
private val SurfaceContainerLowest = Color(0xFF120A0B)
private val SurfaceContainerLow = Color(0xFF201417)
private val SurfaceContainer = Color(0xFF261A1D)
private val SurfaceContainerHigh = Color(0xFF312326)
private val SurfaceContainerHighest = Color(0xFF3C2D30)
private val OnSurface = Color(0xFFF3ECE4)
private val OnSurfaceVariant = Color(0xFFD2C3BE)
private val InverseSurface = Color(0xFFF3ECE4)
private val InverseOnSurface = Color(0xFF33292B)
private val Outline = Color(0xFF9D8884)
private val OutlineVariant = Color(0xFF4C3A3C)
private val SurfaceTint = Color(0xFF6FD3CF)
private val Primary = Color(0xFF86DAD5)
private val OnPrimary = Color(0xFF00373A)
private val PrimaryContainer = Color(0xFF3DB5AF)
private val OnPrimaryContainer = Color(0xFF00403F)
private val InversePrimary = Color(0xFF00696A)
private val Secondary = Color(0xFFF0B3AC)
private val OnSecondary = Color(0xFF4C1E1D)
private val SecondaryContainer = Color(0xFFA24A47)
private val OnSecondaryContainer = Color(0xFFFFDAD6)
private val Tertiary = Color(0xFFB3CBD3)
private val OnTertiary = Color(0xFF1C3640)
private val TertiaryContainer = Color(0xFF69848E)
private val OnTertiaryContainer = Color(0xFF0A2229)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF180E10)
private val OnBackground = Color(0xFFF3ECE4)
private val SurfaceVariant = Color(0xFF3C2D30)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Teal = Color(0xFF3DB5AF)
private val TealPressed = Color(0xFF1F8F8A)
private val TealDeep = Color(0xFF14504F)
private val TealLight = Color(0xFF86DAD5)
private val Gold = Color(0xFFE2BF6A)
private val Amber = Color(0xFFF0B64A)
private val Leaf = Color(0xFF7BC96F)
private val Coral = Color(0xFFF2625E)
private val Rose = Color(0xFFFF7A9A)
private val Violet = Color(0xFFB9A0F2)
private val Slate = Color(0xFF7A8A8F)
private val SurfaceCard = Color(0xFF2A1B1F)
private val SurfaceElevated = Color(0xFF382529)
private val SurfaceInset = Color(0xFF1F1316)
private val BorderSubtle = Color(0xFF4A3236)

// Release-type pills on Discover's Trending list:
// sky for new releases, since teal is this theme's accent.
private val ReleaseNewColor = Color(0xFF7DD3FC)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val SayaNoUtaColorScheme = darkColorScheme(
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

private val SayaNoUtaColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Teal.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Teal.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = TealPressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Teal,
    accentPressed = TealPressed,
    onAccent = Surface,
    accentDeep = TealDeep,
    score = Gold,
    placeholder = Slate,
    spoilerNone = tinted(Leaf),
    spoilerMinor = tinted(Amber),
    spoilerMajor = tintedStrong(Coral),
    tagNsfw = tinted(Rose),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(ReleaseNewColor),
    releaseTranslation = tinted(ReleaseTranslationColor),
    releasePatch = tinted(ReleasePatchColor),
    statusPlaying = tinted(Teal),
    statusFinished = tinted(Leaf),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(Coral),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = TealDeep,
    chipActiveContent = TealLight,
)

/** Wine surfaces, cream text and a teal accent. */
internal val SayaNoUtaScheme = VndbScheme(
    colorScheme = SayaNoUtaColorScheme,
    colors = SayaNoUtaColors,
)
