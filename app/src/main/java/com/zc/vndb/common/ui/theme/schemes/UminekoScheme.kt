package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Umineko: ebony surfaces with the blue truth as accent, the red truth as secondary and the golden butterflies as
// score.
// Anchors: background #110A0C, cards #1E1215, text #F2E9E6, accent #5B8DFF, secondary #E8414F, score #E8C060.
// Values and contrast checks: docs/plans/14-more-themes.md, Appendix A.

// Material 3 roles
private val Surface = Color(0xFF110A0C)
private val SurfaceDim = Color(0xFF110A0C)
private val SurfaceBright = Color(0xFF403436)
private val SurfaceContainerLowest = Color(0xFF0B0608)
private val SurfaceContainerLow = Color(0xFF170E10)
private val SurfaceContainer = Color(0xFF1E1215)
private val SurfaceContainerHigh = Color(0xFF291D1F)
private val SurfaceContainerHighest = Color(0xFF33282A)
private val OnSurface = Color(0xFFF2E9E6)
private val OnSurfaceVariant = Color(0xFFBAB1B0)
private val InverseSurface = Color(0xFFF2E9E6)
private val InverseOnSurface = Color(0xFF261B1D)
private val Outline = Color(0xFF8D8584)
private val OutlineVariant = Color(0xFF3A2E30)
private val SurfaceTint = Color(0xFF8CAFFF)
private val Primary = Color(0xFFA5C0FF)
private val OnPrimary = Color(0xFF1B2A4D)
private val PrimaryContainer = Color(0xFF5B8DFF)
private val OnPrimaryContainer = Color(0xFF0A0607)
private val InversePrimary = Color(0xFF4971CC)
private val Secondary = Color(0xFFF0848D)
private val OnSecondary = Color(0xFF411216)
private val SecondaryContainer = Color(0xFF742028)
private val OnSecondaryContainer = Color(0xFFFAD9DC)
private val Tertiary = Color(0xFFEED088)
private val OnTertiary = Color(0xFF41361B)
private val TertiaryContainer = Color(0xFF615128)
private val OnTertiaryContainer = Color(0xFFFCF6E7)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF110A0C)
private val OnBackground = Color(0xFFF2E9E6)
private val SurfaceVariant = Color(0xFF33282A)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Accent = Color(0xFF5B8DFF)
private val AccentPressed = Color(0xFF476EC7)
private val AccentDeep = Color(0xFF262F50)
private val AccentTint = Color(0xFF94B5FF)
private val OnAccent = Color(0xFF0A0607)
private val Score = Color(0xFFE8C060)
private val Placeholder = Color(0xFF827A79)
private val SurfaceCard = Color(0xFF1E1215)
private val SurfaceElevated = Color(0xFF2D2124)
private val SurfaceInset = Color(0xFF160D0F)
private val BorderSubtle = Color(0xFF312528)
private val PlayingColor = Color(0xFFE8C060)
private val FinishedColor = Color(0xFF5FD3A5)
private val StalledColor = Color(0xFFF0B84A)
private val DroppedColor = Color(0xFFFF6B78)
private val NsfwColor = Color(0xFFFF8FB1)
private val WishlistColor = Color(0xFFB5A2F2)

// Release-type pills on Discover's Trending list
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFC4B5FD)
private val ReleasePatchColor = Color(0xFFFB923C)

private val UminekoColorScheme = darkColorScheme(
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

private val UminekoColors = VndbColors(
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

/** Ebony surfaces, pale rose text, a blue-truth accent and a red-truth secondary. */
internal val UminekoScheme = VndbScheme(
    colorScheme = UminekoColorScheme,
    colors = UminekoColors,
)
