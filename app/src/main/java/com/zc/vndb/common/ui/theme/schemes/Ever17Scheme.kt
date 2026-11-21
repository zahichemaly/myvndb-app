package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Ever17: the deep-sea teal of LeMU with a coral accent and aqua secondary. The release-new pill is mint, since
// teal is the surface.
// Anchors: background #04141A, cards #0B232B, text #DDF3F6, accent #FF7A66, secondary #4FD8E0, score #F2C879.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF04141A)
private val SurfaceDim = Color(0xFF04141A)
private val SurfaceBright = Color(0xFF2D444B)
private val SurfaceContainerLowest = Color(0xFF030D11)
private val SurfaceContainerLow = Color(0xFF071B22)
private val SurfaceContainer = Color(0xFF0B232B)
private val SurfaceContainerHigh = Color(0xFF162D35)
private val SurfaceContainerHighest = Color(0xFF20383F)
private val OnSurface = Color(0xFFDDF3F6)
private val OnSurfaceVariant = Color(0xFFA7BBBF)
private val InverseSurface = Color(0xFFDDF3F6)
private val InverseOnSurface = Color(0xFF132B33)
private val Outline = Color(0xFF7B8F93)
private val OutlineVariant = Color(0xFF263E45)
private val SurfaceTint = Color(0xFFFFA294)
private val Primary = Color(0xFFFFB6AB)
private val OnPrimary = Color(0xFF4D251F)
private val PrimaryContainer = Color(0xFFFF7A66)
private val OnPrimaryContainer = Color(0xFF020C10)
private val InversePrimary = Color(0xFFCC6252)
private val Secondary = Color(0xFF8DE6EB)
private val OnSecondary = Color(0xFF163C3F)
private val SecondaryContainer = Color(0xFF286C70)
private val OnSecondaryContainer = Color(0xFFDCF7F9)
private val Tertiary = Color(0xFFF5D69A)
private val OnTertiary = Color(0xFF443822)
private val TertiaryContainer = Color(0xFF665433)
private val OnTertiaryContainer = Color(0xFFFDF7EB)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF04141A)
private val OnBackground = Color(0xFFDDF3F6)
private val SurfaceVariant = Color(0xFF20383F)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFFFF7A66)
private val AccentPressed = Color(0xFFC75F50)
private val AccentDeep = Color(0xFF4A312F)
private val AccentTint = Color(0xFFFFA99C)
private val OnAccent = Color(0xFF020C10)
private val Score = Color(0xFFF2C879)
private val Placeholder = Color(0xFF708488)
private val SurfaceCard = Color(0xFF0B232B)
private val SurfaceElevated = Color(0xFF1A3239)
private val SurfaceInset = Color(0xFF061920)
private val BorderSubtle = Color(0xFF1E363D)
private val PlayingColor = Color(0xFF4FD8E0)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFFF8FA3)
private val NsfwColor = Color(0xFFF9A8D4)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF86EFAC)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFDBA74)

private val Ever17ColorScheme = darkColorScheme(
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

private val Ever17Colors = VndbColors(
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

/** Abyssal teal surfaces, pale aqua text and a coral accent. */
internal val Ever17Scheme = VndbScheme(
    colorScheme = Ever17ColorScheme,
    colors = Ever17Colors,
)
