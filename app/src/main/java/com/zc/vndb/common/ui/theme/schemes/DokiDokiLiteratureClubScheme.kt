package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Doki Doki Literature Club!: "Just Monika". Plum surfaces, an emerald accent (Monika's eyes) and club pink as
// secondary. "Finished" is sky blue, since green is the accent.
// Anchors: background #160E15, cards #251723, text #F8EAF3, accent #3FC47E, secondary #FF8AC0, score #F7D774.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF160E15)
private val SurfaceDim = Color(0xFF160E15)
private val SurfaceBright = Color(0xFF473944)
private val SurfaceContainerLowest = Color(0xFF0E090E)
private val SurfaceContainerLow = Color(0xFF1D121B)
private val SurfaceContainer = Color(0xFF251723)
private val SurfaceContainerHigh = Color(0xFF30222D)
private val SurfaceContainerHighest = Color(0xFF3A2C38)
private val OnSurface = Color(0xFFF8EAF3)
private val OnSurfaceVariant = Color(0xFFC0B3BC)
private val InverseSurface = Color(0xFFF8EAF3)
private val InverseOnSurface = Color(0xFF2D1F2B)
private val Outline = Color(0xFF92878F)
private val OutlineVariant = Color(0xFF40323E)
private val SurfaceTint = Color(0xFF79D6A5)
private val Primary = Color(0xFF95DFB8)
private val OnPrimary = Color(0xFF133B26)
private val PrimaryContainer = Color(0xFF3FC47E)
private val OnPrimaryContainer = Color(0xFF0D080D)
private val InversePrimary = Color(0xFF329D65)
private val Secondary = Color(0xFFFFB3D6)
private val OnSecondary = Color(0xFF472736)
private val SecondaryContainer = Color(0xFF804560)
private val OnSecondaryContainer = Color(0xFFFFE8F2)
private val Tertiary = Color(0xFFF9E197)
private val OnTertiary = Color(0xFF453C20)
private val TertiaryContainer = Color(0xFF685A31)
private val OnTertiaryContainer = Color(0xFFFEF9EA)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF160E15)
private val OnBackground = Color(0xFFF8EAF3)
private val SurfaceVariant = Color(0xFF3A2C38)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFF3FC47E)
private val AccentPressed = Color(0xFF319962)
private val AccentDeep = Color(0xFF214132)
private val AccentTint = Color(0xFF82D9AB)
private val OnAccent = Color(0xFF0D080D)
private val Score = Color(0xFFF7D774)
private val Placeholder = Color(0xFF877C84)
private val SurfaceCard = Color(0xFF251723)
private val SurfaceElevated = Color(0xFF342632)
private val SurfaceInset = Color(0xFF1B111A)
private val BorderSubtle = Color(0xFF382A36)
private val PlayingColor = Color(0xFFFF8AC0)
private val FinishedColor = Color(0xFF7DD3FC)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFF0617F)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val DokiDokiLiteratureClubColorScheme = darkColorScheme(
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

private val DokiDokiLiteratureClubColors = VndbColors(
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

/** Plum surfaces, pink-white text and an emerald accent. */
internal val DokiDokiLiteratureClubScheme = VndbScheme(
    colorScheme = DokiDokiLiteratureClubColorScheme,
    colors = DokiDokiLiteratureClubColors,
)
