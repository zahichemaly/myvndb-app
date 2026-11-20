package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted

// House in Fata Morgana: warm near-black surfaces, a muted gold accent and a dusty crimson.
// Anchors: background #171311, cards #261F1B, accent gold #C9A66B, secondary wine #8C4A4A.
// The ivory text (#E7E5D8), sage-green tertiary and violet label come from the wallpaper.

// Material 3 roles
private val Surface = Color(0xFF171311)
private val SurfaceDim = Color(0xFF171311)
private val SurfaceBright = Color(0xFF433A33)
private val SurfaceContainerLowest = Color(0xFF110E0D)
private val SurfaceContainerLow = Color(0xFF1E1916)
private val SurfaceContainer = Color(0xFF261F1B)
private val SurfaceContainerHigh = Color(0xFF2F2823)
private val SurfaceContainerHighest = Color(0xFF3A322C)
private val OnSurface = Color(0xFFE7E5D8)
private val OnSurfaceVariant = Color(0xFFBDB2A2)
private val InverseSurface = Color(0xFFE7E5D8)
private val InverseOnSurface = Color(0xFF322B26)
private val Outline = Color(0xFF8F8475)
private val OutlineVariant = Color(0xFF453B33)
private val SurfaceTint = Color(0xFFD9B87A)
private val Primary = Color(0xFFE3C892)
private val OnPrimary = Color(0xFF3F2D05)
private val PrimaryContainer = Color(0xFFC9A66B)
private val OnPrimaryContainer = Color(0xFF3A2900)
private val InversePrimary = Color(0xFF7A5B1E)
private val Secondary = Color(0xFFE0A8A6)
private val OnSecondary = Color(0xFF4A1D1D)
private val SecondaryContainer = Color(0xFF8C4A4A)
private val OnSecondaryContainer = Color(0xFFFFDAD6)
private val Tertiary = Color(0xFFA8C7C2)
private val OnTertiary = Color(0xFF123532)
private val TertiaryContainer = Color(0xFF5F8581)
private val OnTertiaryContainer = Color(0xFF031F1D)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF171311)
private val OnBackground = Color(0xFFE7E5D8)
private val SurfaceVariant = Color(0xFF3A322C)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors
private val Gold = Color(0xFFC9A66B)
private val GoldPressed = Color(0xFFA8884F)
private val GoldDeep = Color(0xFF4A3A1E)
private val GoldLight = Color(0xFFE3C892)
private val Wine = Color(0xFF8C4A4A)
private val Amber = Color(0xFFE8B44A)
private val Orange = Color(0xFFD98A4A)
private val Sage = Color(0xFF8DB580)
private val Coral = Color(0xFFD9726E)
private val Rose = Color(0xFFE58A9A)
private val Violet = Color(0xFFA98BD0)
private val Slate = Color(0xFF8A8378)
private val SurfaceCard = Color(0xFF261F1B)
private val SurfaceElevated = Color(0xFF322A24)
private val SurfaceInset = Color(0xFF1E1916)
private val BorderSubtle = Color(0xFF3F352E)

private const val WINE_CONTAINER_ALPHA = 0.3f

/** The warning chip: wine fill and border with coral text, so danger reads as this theme's red. */
private val WineWarning = TintedColors(
    container = Wine.copy(alpha = WINE_CONTAINER_ALPHA),
    content = Coral,
    border = Wine,
)

private val HouseInFataMorganaColorScheme = darkColorScheme(
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

private val HouseInFataMorganaColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Gold.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Gold.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = GoldPressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Gold,
    accentPressed = GoldPressed,
    onAccent = Surface,
    accentDeep = GoldDeep,
    score = Amber,
    placeholder = Slate,
    spoilerNone = tinted(Sage),
    spoilerMinor = tinted(Amber),
    spoilerMajor = WineWarning,
    tagNsfw = tinted(Rose),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(Tertiary),
    releaseTranslation = tinted(Gold),
    releasePatch = tinted(Orange),
    releaseRerelease = tinted(Sage),
    statusPlaying = tinted(Gold),
    statusFinished = tinted(Sage),
    statusStalled = tinted(Orange),
    statusDropped = WineWarning,
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = GoldDeep,
    chipActiveContent = GoldLight,
)

/** Warm near-black surfaces, ivory text, a muted gold accent and a dusty wine red. */
internal val HouseInFataMorganaScheme = VndbScheme(
    colorScheme = HouseInFataMorganaColorScheme,
    colors = HouseInFataMorganaColors,
)
