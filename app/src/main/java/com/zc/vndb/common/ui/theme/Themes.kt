package com.zc.vndb.common.ui.theme

import androidx.annotation.DrawableRes
import com.zc.vndb.common.ui.resource.DrawableResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.schemes.AmoledScheme
import com.zc.vndb.common.ui.theme.schemes.ClannadScheme
import com.zc.vndb.common.ui.theme.schemes.DanganronpaScheme
import com.zc.vndb.common.ui.theme.schemes.DarkScheme
import com.zc.vndb.common.ui.theme.schemes.DokiDokiLiteratureClubScheme
import com.zc.vndb.common.ui.theme.schemes.EfScheme
import com.zc.vndb.common.ui.theme.schemes.Ever17Scheme
import com.zc.vndb.common.ui.theme.schemes.FateStayNightScheme
import com.zc.vndb.common.ui.theme.schemes.HigurashiScheme
import com.zc.vndb.common.ui.theme.schemes.HouseInFataMorganaScheme
import com.zc.vndb.common.ui.theme.schemes.KaraNoShoujoScheme
import com.zc.vndb.common.ui.theme.schemes.LightScheme
import com.zc.vndb.common.ui.theme.schemes.MeikeiNoLupercaliaScheme
import com.zc.vndb.common.ui.theme.schemes.MuvLuvAlternativeScheme
import com.zc.vndb.common.ui.theme.schemes.NeonScheme
import com.zc.vndb.common.ui.theme.schemes.NocturneScheme
import com.zc.vndb.common.ui.theme.schemes.SayaNoUtaScheme
import com.zc.vndb.common.ui.theme.schemes.SepiaScheme
import com.zc.vndb.common.ui.theme.schemes.SteinsGateScheme
import com.zc.vndb.common.ui.theme.schemes.TsukihimeScheme
import com.zc.vndb.common.ui.theme.schemes.UminekoScheme
import com.zc.vndb.common.ui.theme.schemes.WhiteAlbum2Scheme

// Every theme: a name, a color scheme from `schemes/` and an optional wallpaper. To add one, see
// docs/plans/10-themes.md ("Adding a theme").

/** A wallpaper kept low so text and cards stay readable over it. */
private const val WALLPAPER_ALPHA = 0.2f

/** How opaque cards are over a wallpaper: low enough for it to show through, high enough to read. */
private const val CARD_ALPHA = 0.7f

private fun wallpaper(@DrawableRes image: Int) = WallpaperSpec(image, WALLPAPER_ALPHA, CARD_ALPHA)

private fun VndbScheme.toSpec(name: UiText, wallpaper: WallpaperSpec? = null) = VndbThemeSpec(
    name = name,
    colorScheme = colorScheme,
    colors = colors,
    wallpaper = wallpaper,
)

/** Nocturne VN on a plain background. */
val OriginalThemeSpec = NocturneScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_original),
)

/** Dark text on a cool off-white, on a plain background. */
val LightThemeSpec = LightScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_light),
)

/** Neutral charcoal greys on a plain background. */
val DarkThemeSpec = DarkScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_dark),
)

/** Neon green on black, on a plain background. */
val NeonThemeSpec = NeonScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_neon),
)

/** Warm paper and ink with a burnt-sienna accent, on a plain background. */
val SepiaThemeSpec = SepiaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_sepia),
)

/** Original's sky blue and amber on true black, on a plain background. */
val AmoledThemeSpec = AmoledScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_amoled),
)

/** Nocturne VN over the Angelic Serenade wallpaper. */
val AngelicSerenadeThemeSpec = NocturneScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_angelic_serenade),
    wallpaper = wallpaper(DrawableResource.wallpaper_angelic_serenade),
)

/** Wine and teal over the Saya no Uta wallpaper. */
val SayaNoUtaThemeSpec = SayaNoUtaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_saya_no_uta),
    wallpaper = wallpaper(DrawableResource.wallpaper_saya_no_uta),
)

/** Warm near-black and gold over the House in Fata Morgana wallpaper. */
val HouseInFataMorganaThemeSpec = HouseInFataMorganaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_house_in_fata_morgana),
    wallpaper = wallpaper(DrawableResource.wallpaper_house_in_fata_morgana),
)

/** Midnight blue and crimson over the Tsukihime wallpaper. */
val TsukihimeThemeSpec = TsukihimeScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_tsukihime),
    wallpaper = wallpaper(DrawableResource.wallpaper_tsukihime),
)

/** Violet-black and gold with rose-crimson over the Meikei no Lupercalia wallpaper. */
val MeikeiNoLupercaliaThemeSpec = MeikeiNoLupercaliaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_meikei_no_lupercalia),
    wallpaper = wallpaper(DrawableResource.wallpaper_meikei_no_lupercalia),
)

/** Olive-black and Nixie amber over the Steins;Gate wallpaper. */
val SteinsGateThemeSpec = SteinsGateScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_steins_gate),
    wallpaper = wallpaper(DrawableResource.wallpaper_steins_gate),
)

/** Deep-sea teal and coral over the Ever17 wallpaper. */
val Ever17ThemeSpec = Ever17Scheme.toSpec(
    name = UiText.StringResource(StringResource.theme_ever17),
    wallpaper = wallpaper(DrawableResource.wallpaper_ever17),
)

/** Blush white and sakura rose over the Clannad wallpaper. */
val ClannadThemeSpec = ClannadScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_clannad),
    wallpaper = wallpaper(DrawableResource.wallpaper_clannad),
)

/** Forest green and blood red over the Higurashi wallpaper. */
val HigurashiThemeSpec = HigurashiScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_higurashi),
    wallpaper = wallpaper(DrawableResource.wallpaper_higurashi),
)

/** Plum, emerald and club pink over the Doki Doki Literature Club! wallpaper. */
val DokiDokiLiteratureClubThemeSpec = DokiDokiLiteratureClubScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_doki_doki_literature_club),
    wallpaper = wallpaper(DrawableResource.wallpaper_doki_doki_literature_club),
)

/** Gunmetal and hazard yellow over the Muv-Luv Alternative wallpaper. */
val MuvLuvAlternativeThemeSpec = MuvLuvAlternativeScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_muv_luv_alternative),
    wallpaper = wallpaper(DrawableResource.wallpaper_muv_luv_alternative),
)

/** Black, white and shocking pink over the Danganronpa wallpaper. */
val DanganronpaThemeSpec = DanganronpaScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_danganronpa),
    wallpaper = wallpaper(DrawableResource.wallpaper_danganronpa),
)

/** Frost grey and winter rose over the White Album 2 wallpaper. */
val WhiteAlbum2ThemeSpec = WhiteAlbum2Scheme.toSpec(
    name = UiText.StringResource(StringResource.theme_white_album_2),
    wallpaper = wallpaper(DrawableResource.wallpaper_white_album_2),
)

/** Twilight mauve and apricot over the ef - a fairy tale of the two. wallpaper. */
val EfThemeSpec = EfScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_ef),
    wallpaper = wallpaper(DrawableResource.wallpaper_ef),
)

/** Ash black and bone white over the Kara no Shoujo wallpaper. */
val KaraNoShoujoThemeSpec = KaraNoShoujoScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_kara_no_shoujo),
    wallpaper = wallpaper(DrawableResource.wallpaper_kara_no_shoujo),
)

/** Ebony, blue truth and red truth over the Umineko wallpaper. */
val UminekoThemeSpec = UminekoScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_umineko),
    wallpaper = wallpaper(DrawableResource.wallpaper_umineko),
)

/** Steel, royal blue and gold over the Fate/stay night wallpaper. */
val FateStayNightThemeSpec = FateStayNightScheme.toSpec(
    name = UiText.StringResource(StringResource.theme_fate_stay_night),
    wallpaper = wallpaper(DrawableResource.wallpaper_fate_stay_night),
)
