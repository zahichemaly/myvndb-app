package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// AMOLED: Original's sky blue and amber on true black, to save battery on OLED screens. Semantic colors are
// Nocturne's.
// Anchors: background #000000, cards #0F1013, text #F2F4F8, accent #38BDF8, secondary #FFB95F, score #F59E0B.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF000000)
private val SurfaceDim = Color(0xFF000000)
private val SurfaceBright = Color(0xFF333438)
private val SurfaceContainerLowest = Color(0xFF000000)
private val SurfaceContainerLow = Color(0xFF070709)
private val SurfaceContainer = Color(0xFF0F1013)
private val SurfaceContainerHigh = Color(0xFF1A1B1E)
private val SurfaceContainerHighest = Color(0xFF26272A)
private val OnSurface = Color(0xFFF2F4F8)
private val OnSurfaceVariant = Color(0xFFB6B7BA)
private val InverseSurface = Color(0xFFF2F4F8)
private val InverseOnSurface = Color(0xFF18191C)
private val Outline = Color(0xFF858688)
private val OutlineVariant = Color(0xFF2D2E31)
private val SurfaceTint = Color(0xFF74D1FA)
private val Primary = Color(0xFF92DBFB)
private val OnPrimary = Color(0xFF11394A)
private val PrimaryContainer = Color(0xFF38BDF8)
private val OnPrimaryContainer = Color(0xFF000000)
private val InversePrimary = Color(0xFF2D97C6)
private val Secondary = Color(0xFFFFD297)
private val OnSecondary = Color(0xFF47341B)
private val SecondaryContainer = Color(0xFF805C30)
private val OnSecondaryContainer = Color(0xFFFFF1DF)
private val Tertiary = Color(0xFFF8B648)
private val OnTertiary = Color(0xFF452C03)
private val TertiaryContainer = Color(0xFF674205)
private val OnTertiaryContainer = Color(0xFFFEF0DA)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF000000)
private val OnBackground = Color(0xFFF2F4F8)
private val SurfaceVariant = Color(0xFF26272A)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFF38BDF8)
private val AccentPressed = Color(0xFF2C93C1)
private val AccentDeep = Color(0xFF103545)
private val AccentTint = Color(0xFF7ED4FA)
private val OnAccent = Color(0xFF000000)
private val Score = Color(0xFFF59E0B)
private val Placeholder = Color(0xFF797A7C)
private val SurfaceCard = Color(0xFF0F1013)
private val SurfaceElevated = Color(0xFF1F2023)
private val SurfaceInset = Color(0xFF050607)
private val BorderSubtle = Color(0xFF232528)
private val PlayingColor = Color(0xFF38BDF8)
private val FinishedColor = Color(0xFF10B981)
private val StalledColor = Color(0xFFF59E0B)
private val DroppedColor = Color(0xFFF05252)
private val NsfwColor = Color(0xFFF43F5E)
private val WishlistColor = Color(0xFFA78BFA)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val AmoledColorScheme = darkColorScheme(
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

private val AmoledColors = VndbColors(
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

/** AMOLED: true-black surfaces with Original's sky-blue accent and amber score. */
internal val AmoledScheme = VndbScheme(
    colorScheme = AmoledColorScheme,
    colors = AmoledColors,
)
