package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Fate/stay night: Saber's armor steel with a royal-blue accent and a gold secondary and score.
// Anchors: background #0E1015, cards #1A1E27, text #ECEEF3, accent #6B8EF2, secondary #E3B341, score #E3B341.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF0E1015)
private val SurfaceDim = Color(0xFF0E1015)
private val SurfaceBright = Color(0xFF3C3F48)
private val SurfaceContainerLowest = Color(0xFF090A0E)
private val SurfaceContainerLow = Color(0xFF13161D)
private val SurfaceContainer = Color(0xFF1A1E27)
private val SurfaceContainerHigh = Color(0xFF242831)
private val SurfaceContainerHighest = Color(0xFF2F333B)
private val OnSurface = Color(0xFFECEEF3)
private val OnSurfaceVariant = Color(0xFFB4B6BC)
private val InverseSurface = Color(0xFFECEEF3)
private val InverseOnSurface = Color(0xFF22262F)
private val Outline = Color(0xFF888A8F)
private val OutlineVariant = Color(0xFF353942)
private val SurfaceTint = Color(0xFF97B0F6)
private val Primary = Color(0xFFAEC1F8)
private val OnPrimary = Color(0xFF202B49)
private val PrimaryContainer = Color(0xFF6B8EF2)
private val OnPrimaryContainer = Color(0xFF080A0D)
private val InversePrimary = Color(0xFF5672C2)
private val Secondary = Color(0xFFEDCE84)
private val OnSecondary = Color(0xFF403212)
private val SecondaryContainer = Color(0xFF725A20)
private val OnSecondaryContainer = Color(0xFFF9F0D9)
private val Tertiary = Color(0xFFEAC670)
private val OnTertiary = Color(0xFF403212)
private val TertiaryContainer = Color(0xFF5F4B1B)
private val OnTertiaryContainer = Color(0xFFFBF4E2)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0E1015)
private val OnBackground = Color(0xFFECEEF3)
private val SurfaceVariant = Color(0xFF2F333B)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFF6B8EF2)
private val AccentPressed = Color(0xFF536FBD)
private val AccentDeep = Color(0xFF283353)
private val AccentTint = Color(0xFF9FB6F7)
private val OnAccent = Color(0xFF080A0D)
private val Score = Color(0xFFE3B341)
private val Placeholder = Color(0xFF7D7F84)
private val SurfaceCard = Color(0xFF1A1E27)
private val SurfaceElevated = Color(0xFF292D35)
private val SurfaceInset = Color(0xFF12151B)
private val BorderSubtle = Color(0xFF2D3139)
private val PlayingColor = Color(0xFF9FB4F7)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFFDBA74)
private val DroppedColor = Color(0xFFF0617F)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val FateStayNightColorScheme = darkColorScheme(
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

private val FateStayNightColors = VndbColors(
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

/** Steel surfaces, cool white text, a royal-blue accent and gold. */
internal val FateStayNightScheme = VndbScheme(
    colorScheme = FateStayNightColorScheme,
    colors = FateStayNightColors,
)
