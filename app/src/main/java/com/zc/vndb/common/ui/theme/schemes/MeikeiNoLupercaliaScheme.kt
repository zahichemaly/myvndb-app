package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Meikei no Lupercalia: a violet-black night with a gold accent and rose-crimson highlights.
// Anchors: background #0D0B14, cards #181524, text #F3EFFA, muted text #9E97B0, gold #E2B86B, rose #D94B6A.
// The rose-crimson (secondary, dropped, major spoilers) echoes the wallpaper's red dress and roses.

// Material 3 roles
private val Surface = Color(0xFF0D0B14)
private val SurfaceDim = Color(0xFF0D0B14)
private val SurfaceBright = Color(0xFF2E2942)
private val SurfaceContainerLowest = Color(0xFF09070F)
private val SurfaceContainerLow = Color(0xFF120F1C)
private val SurfaceContainer = Color(0xFF181524)
private val SurfaceContainerHigh = Color(0xFF201C2F)
private val SurfaceContainerHighest = Color(0xFF292439)
private val OnSurface = Color(0xFFF3EFFA)
private val OnSurfaceVariant = Color(0xFF9E97B0)
private val InverseSurface = Color(0xFFF3EFFA)
private val InverseOnSurface = Color(0xFF241F33)
private val Outline = Color(0xFF7C7690)
private val OutlineVariant = Color(0xFF2E2942)
private val SurfaceTint = Color(0xFFE2B86B)
private val Primary = Color(0xFFE2B86B)
private val OnPrimary = Color(0xFF3A2704)
private val PrimaryContainer = Color(0xFF5C4312)
private val OnPrimaryContainer = Color(0xFFFFE2A8)
private val InversePrimary = Color(0xFF8A6420)
private val Secondary = Color(0xFFF08CA2)
private val OnSecondary = Color(0xFF5A0F25)
private val SecondaryContainer = Color(0xFF8F2A44)
private val OnSecondaryContainer = Color(0xFFFFD9E1)
private val Tertiary = Color(0xFFB5A2F2)
private val OnTertiary = Color(0xFF2A1B5C)
private val TertiaryContainer = Color(0xFF4A3A85)
private val OnTertiaryContainer = Color(0xFFE6DEFF)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0D0B14)
private val OnBackground = Color(0xFFF3EFFA)
private val SurfaceVariant = Color(0xFF292439)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors. Gold is the brand accent; rose-crimson is kept for "dropped" and major spoilers,
// and "playing" is sky blue.
private val Accent = Color(0xFFE2B86B)
private val GoldPressed = Color(0xFFB8924A)
private val GoldDeep = Color(0xFF3D2E12)
private val RoseLight = Color(0xFFF08CA2)
private val GoldTint = Color(0xFFF0CF8E)
private val OnGold = Color(0xFF2A1C05)
private val Sky = Color(0xFF8FA8F0)
private val Gold = Color(0xFFF5D98F)
private val Amber = Color(0xFFF0B84A)
private val Mint = Color(0xFF5FD3A5)
private val Rose = Color(0xFFD94B6A)
private val Violet = Color(0xFFB5A2F2)
private val Slate = Color(0xFF7C7690)
private val SurfaceCard = Color(0xFF181524)
private val SurfaceElevated = Color(0xFF221E33)
private val SurfaceInset = Color(0xFF120F1C)
private val BorderSubtle = Color(0xFF2E2942)

// Release-type pills on Discover's Trending list:
// periwinkle for patches, since orange sits too close to the gold accent.
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFF8FA8F0)

private val MeikeiNoLupercaliaColorScheme = darkColorScheme(
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

private val MeikeiNoLupercaliaColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Accent.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Accent.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = GoldPressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Accent,
    accentPressed = GoldPressed,
    onAccent = OnGold,
    accentDeep = GoldDeep,
    score = Gold,
    placeholder = Slate,
    spoilerNone = tinted(Mint),
    spoilerMinor = tinted(Amber),
    spoilerMajor = tintedStrong(RoseLight),
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
    statusFinished = tinted(Mint),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(RoseLight),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = GoldDeep,
    chipActiveContent = GoldTint,
)

/** Violet-black surfaces, pale lavender-white text and a gold accent. */
internal val MeikeiNoLupercaliaScheme = VndbScheme(
    colorScheme = MeikeiNoLupercaliaColorScheme,
    colors = MeikeiNoLupercaliaColors,
)
