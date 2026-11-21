package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// White Album 2: a snowy night. Frost-grey surfaces, a winter-rose accent (Setsuna's scarf) and an ice-blue
// secondary.
// Anchors: background #0F1418, cards #1A2329, text #EEF3F7, accent #E8809A, secondary #9CC7E6, score #F2D08A.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF0F1418)
private val SurfaceDim = Color(0xFF0F1418)
private val SurfaceBright = Color(0xFF3C444A)
private val SurfaceContainerLowest = Color(0xFF0A0D10)
private val SurfaceContainerLow = Color(0xFF141B20)
private val SurfaceContainer = Color(0xFF1A2329)
private val SurfaceContainerHigh = Color(0xFF252D33)
private val SurfaceContainerHighest = Color(0xFF2F383E)
private val OnSurface = Color(0xFFEEF3F7)
private val OnSurfaceVariant = Color(0xFFB6BBBF)
private val InverseSurface = Color(0xFFEEF3F7)
private val InverseOnSurface = Color(0xFF222B31)
private val Outline = Color(0xFF8A8F93)
private val OutlineVariant = Color(0xFF363E44)
private val SurfaceTint = Color(0xFFEFA6B8)
private val Primary = Color(0xFFF2B9C7)
private val OnPrimary = Color(0xFF46262E)
private val PrimaryContainer = Color(0xFFE8809A)
private val OnPrimaryContainer = Color(0xFF090C0E)
private val InversePrimary = Color(0xFFBA667B)
private val Secondary = Color(0xFFBFDBEF)
private val OnSecondary = Color(0xFF2C3840)
private val SecondaryContainer = Color(0xFF4E6473)
private val OnSecondaryContainer = Color(0xFFEBF4FA)
private val Tertiary = Color(0xFFF5DCA7)
private val OnTertiary = Color(0xFF443A27)
private val TertiaryContainer = Color(0xFF66573A)
private val OnTertiaryContainer = Color(0xFFFDF8ED)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0F1418)
private val OnBackground = Color(0xFFEEF3F7)
private val SurfaceVariant = Color(0xFF2F383E)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFE8809A)
private val AccentPressed = Color(0xFFB56478)
private val AccentDeep = Color(0xFF4C323C)
private val AccentTint = Color(0xFFF0ACBD)
private val OnAccent = Color(0xFF090C0E)
private val Score = Color(0xFFF2D08A)
private val Placeholder = Color(0xFF7E8488)
private val SurfaceCard = Color(0xFF1A2329)
private val SurfaceElevated = Color(0xFF293237)
private val SurfaceInset = Color(0xFF13191E)
private val BorderSubtle = Color(0xFF2D363C)
private val PlayingColor = Color(0xFF9CC7E6)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFFF7A7A)
private val NsfwColor = Color(0xFFF0ABFC)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val WhiteAlbum2ColorScheme = darkColorScheme(
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

private val WhiteAlbum2Colors = VndbColors(
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

/** Frost-grey surfaces, snow-white text and a winter-rose accent. */
internal val WhiteAlbum2Scheme = VndbScheme(
    colorScheme = WhiteAlbum2ColorScheme,
    colors = WhiteAlbum2Colors,
)
