package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Higurashi: Hinamizawa's forest at dusk, a blood-red accent and a cicada-amber secondary. "Playing" is moss green;
// only "dropped" and major spoilers use the lighter red.
// Anchors: background #0B130E, cards #15211A, text #E3ECE0, accent #EE5A5A, secondary #E8A04F, score #E8C36A.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF0B130E)
private val SurfaceDim = Color(0xFF0B130E)
private val SurfaceBright = Color(0xFF36413A)
private val SurfaceContainerLowest = Color(0xFF070C09)
private val SurfaceContainerLow = Color(0xFF101913)
private val SurfaceContainer = Color(0xFF15211A)
private val SurfaceContainerHigh = Color(0xFF1F2B24)
private val SurfaceContainerHighest = Color(0xFF2A352E)
private val OnSurface = Color(0xFFE3ECE0)
private val OnSurfaceVariant = Color(0xFFADB6AC)
private val InverseSurface = Color(0xFFE3ECE0)
private val InverseOnSurface = Color(0xFF1D2922)
private val Outline = Color(0xFF828A82)
private val OutlineVariant = Color(0xFF303B34)
private val SurfaceTint = Color(0xFFF38C8C)
private val Primary = Color(0xFFF6A4A4)
private val OnPrimary = Color(0xFF471B1B)
private val PrimaryContainer = Color(0xFFEE5A5A)
private val OnPrimaryContainer = Color(0xFF070B08)
private val InversePrimary = Color(0xFFBE4848)
private val Secondary = Color(0xFFF0C18D)
private val OnSecondary = Color(0xFF412D16)
private val SecondaryContainer = Color(0xFF745028)
private val OnSecondaryContainer = Color(0xFFFAECDC)
private val Tertiary = Color(0xFFEED28F)
private val OnTertiary = Color(0xFF41371E)
private val TertiaryContainer = Color(0xFF61522D)
private val OnTertiaryContainer = Color(0xFFFCF6E9)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0B130E)
private val OnBackground = Color(0xFFE3ECE0)
private val SurfaceVariant = Color(0xFF2A352E)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFEE5A5A)
private val AccentPressed = Color(0xFFBA4646)
private val AccentDeep = Color(0xFF4B2723)
private val AccentTint = Color(0xFFF49494)
private val OnAccent = Color(0xFF070B08)
private val Score = Color(0xFFE8C36A)
private val Placeholder = Color(0xFF778077)
private val SurfaceCard = Color(0xFF15211A)
private val SurfaceElevated = Color(0xFF232F28)
private val SurfaceInset = Color(0xFF0E1812)
private val BorderSubtle = Color(0xFF28332C)
private val PlayingColor = Color(0xFF8FBF7A)
private val FinishedColor = Color(0xFF6EE7B7)
private val StalledColor = Color(0xFFE8A04F)
private val DroppedColor = Color(0xFFFF8A8A)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val HigurashiColorScheme = darkColorScheme(
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

private val HigurashiColors = VndbColors(
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

/** Forest-green surfaces, pale sage text and a blood-red accent. */
internal val HigurashiScheme = VndbScheme(
    colorScheme = HigurashiColorScheme,
    colors = HigurashiColors,
)
