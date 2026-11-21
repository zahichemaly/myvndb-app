package com.zc.vndb.common.ui.theme.schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.TokenAlpha
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbScheme
import com.zc.vndb.common.ui.theme.tinted
import com.zc.vndb.common.ui.theme.tintedStrong

// Tsukihime: a midnight-blue night with a crimson accent.
// Anchors: background #0A0E1A, cards #141C33, text #E6ECFF, accent crimson #EC5570.
// The crimson is light enough to read as text on the cards (4.9:1), so text on it is the dark background
// color (5.6:1) rather than white. See docs/plans/14-more-themes.md, section 9.
// The moon-blue (secondary, "playing") and gold (score) come from the wallpaper's sky and hair.

// Material 3 roles
private val Surface = Color(0xFF0A0E1A)
private val SurfaceDim = Color(0xFF0A0E1A)
private val SurfaceBright = Color(0xFF2B3757)
private val SurfaceContainerLowest = Color(0xFF070A14)
private val SurfaceContainerLow = Color(0xFF0F1426)
private val SurfaceContainer = Color(0xFF141C33)
private val SurfaceContainerHigh = Color(0xFF1B2540)
private val SurfaceContainerHighest = Color(0xFF243050)
private val OnSurface = Color(0xFFE6ECFF)
private val OnSurfaceVariant = Color(0xFFAEB8D6)
private val InverseSurface = Color(0xFFE6ECFF)
private val InverseOnSurface = Color(0xFF1C2338)
private val Outline = Color(0xFF7E89A8)
private val OutlineVariant = Color(0xFF2C3858)
private val SurfaceTint = Color(0xFFF28BA0)
private val Primary = Color(0xFFFFB2BE)
private val OnPrimary = Color(0xFF5C0A1F)
private val PrimaryContainer = Color(0xFFEC5570)
private val OnPrimaryContainer = Color(0xFF0A0E1A)
private val InversePrimary = Color(0xFFBD445A)
private val Secondary = Color(0xFFA9C0F0)
private val OnSecondary = Color(0xFF0F2250)
private val SecondaryContainer = Color(0xFF3F5A99)
private val OnSecondaryContainer = Color(0xFFDCE6FF)
private val Tertiary = Color(0xFFE8C795)
private val OnTertiary = Color(0xFF3F2A07)
private val TertiaryContainer = Color(0xFF9C7B52)
private val OnTertiaryContainer = Color(0xFF2B1A00)
private val Error = Color(0xFFFFB4AB)
private val OnError = Color(0xFF690005)
private val ErrorContainer = Color(0xFF93000A)
private val OnErrorContainer = Color(0xFFFFDAD6)
private val Background = Color(0xFF0A0E1A)
private val OnBackground = Color(0xFFE6ECFF)
private val SurfaceVariant = Color(0xFF243050)
private val Scrim = Color(0xFF000000)

// Brand & semantic colors. Crimson is the brand accent, so red is not used for plain statuses:
// "playing" is moon-blue, and only "dropped" and major spoilers use the lighter crimson.
private val Crimson = Color(0xFFEC5570)
private val CrimsonPressed = Color(0xFFD44C65)
private val CrimsonDeep = Color(0xFF4A1424)
private val CrimsonLight = Color(0xFFFF7D95)
private val CrimsonTint = Color(0xFFFF8FA3)
private val OnCrimson = Color(0xFF0A0E1A)
private val MoonBlue = Color(0xFF7FA6F0)
private val Gold = Color(0xFFF2C879)
private val Amber = Color(0xFFF0B84A)
private val Mint = Color(0xFF5FD3A5)
private val Rose = Color(0xFFFF8FB1)
private val Violet = Color(0xFFB5A2F2)
private val Slate = Color(0xFF7C88A8)
private val SurfaceCard = Color(0xFF141C33)
private val SurfaceElevated = Color(0xFF1E2A4A)
private val SurfaceInset = Color(0xFF0F1528)
private val BorderSubtle = Color(0xFF263253)

// Release-type pills on Discover's Trending list:
// teal, violet and orange stay clear of the crimson accent and gold score.
private val ReleaseNewColor = Color(0xFF2DD4BF)
private val ReleaseTranslationColor = Color(0xFFA78BFA)
private val ReleasePatchColor = Color(0xFFFB923C)

private val TsukihimeColorScheme = darkColorScheme(
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

private val TsukihimeColors = VndbColors(
    surfaceCard = SurfaceCard,
    surfaceElevated = SurfaceElevated,
    surfaceInset = SurfaceInset,
    surfaceGlass = SurfaceInset.copy(alpha = TokenAlpha.GLASS),
    glassHairline = Color.White.copy(alpha = TokenAlpha.HAIRLINE),
    borderSubtle = BorderSubtle,
    borderFocused = Crimson.copy(alpha = TokenAlpha.FOCUS_BORDER),
    focusRing = Crimson.copy(alpha = TokenAlpha.FOCUS_RING),
    glow = CrimsonPressed.copy(alpha = TokenAlpha.GLOW),
    coverScrim = Surface.copy(alpha = TokenAlpha.GLASS),
    accent = Crimson,
    accentPressed = CrimsonPressed,
    onAccent = OnCrimson,
    accentDeep = CrimsonDeep,
    score = Gold,
    placeholder = Slate,
    spoilerNone = tinted(Mint),
    spoilerMinor = tinted(Amber),
    spoilerMajor = tintedStrong(CrimsonLight),
    tagNsfw = tinted(Rose),
    chipNeutral = TintedColors(
        container = SurfaceElevated,
        content = OnSurfaceVariant,
        border = BorderSubtle,
    ),
    releaseNew = tinted(ReleaseNewColor),
    releaseTranslation = tinted(ReleaseTranslationColor),
    releasePatch = tinted(ReleasePatchColor),
    statusPlaying = tinted(MoonBlue),
    statusFinished = tinted(Mint),
    statusStalled = tinted(Amber),
    statusDropped = tintedStrong(CrimsonLight),
    labelWishlist = TintedColors(Color.Transparent, Violet, Violet),
    labelBlacklist = TintedColors(Color.Transparent, OnSurfaceVariant, Slate),
    labelOwn = TintedColors(Color.Transparent, OnSurfaceVariant, BorderSubtle),
    chipInactiveContainer = SurfaceCard,
    chipActiveContainer = CrimsonDeep,
    chipActiveContent = CrimsonTint,
)

/** Midnight-blue surfaces, pale blue-white text and a crimson accent. */
internal val TsukihimeScheme = VndbScheme(
    colorScheme = TsukihimeColorScheme,
    colors = TsukihimeColors,
)
