package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.AppTheme
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.AmoledThemeSpec
import com.zc.vndb.common.ui.theme.AngelicSerenadeThemeSpec
import com.zc.vndb.common.ui.theme.ClannadThemeSpec
import com.zc.vndb.common.ui.theme.DanganronpaThemeSpec
import com.zc.vndb.common.ui.theme.DarkThemeSpec
import com.zc.vndb.common.ui.theme.DokiDokiLiteratureClubThemeSpec
import com.zc.vndb.common.ui.theme.EfThemeSpec
import com.zc.vndb.common.ui.theme.Ever17ThemeSpec
import com.zc.vndb.common.ui.theme.FateStayNightThemeSpec
import com.zc.vndb.common.ui.theme.HigurashiThemeSpec
import com.zc.vndb.common.ui.theme.HouseInFataMorganaThemeSpec
import com.zc.vndb.common.ui.theme.KaraNoShoujoThemeSpec
import com.zc.vndb.common.ui.theme.LightThemeSpec
import com.zc.vndb.common.ui.theme.MeikeiNoLupercaliaThemeSpec
import com.zc.vndb.common.ui.theme.MuvLuvAlternativeThemeSpec
import com.zc.vndb.common.ui.theme.NeonThemeSpec
import com.zc.vndb.common.ui.theme.OriginalThemeSpec
import com.zc.vndb.common.ui.theme.SayaNoUtaThemeSpec
import com.zc.vndb.common.ui.theme.SepiaThemeSpec
import com.zc.vndb.common.ui.theme.SteinsGateThemeSpec
import com.zc.vndb.common.ui.theme.TsukihimeThemeSpec
import com.zc.vndb.common.ui.theme.UminekoThemeSpec
import com.zc.vndb.common.ui.theme.VndbThemeSpec
import com.zc.vndb.common.ui.theme.WhiteAlbum2ThemeSpec

/** The colors, wallpaper and name of this theme. Exhaustive: a new [AppTheme] will not compile without a spec. */
@Suppress("CyclomaticComplexMethod") // A flat one-line branch per theme; a map would lose the exhaustive check.
fun AppTheme.toSpec(): VndbThemeSpec = when (this) {
    AppTheme.Original -> OriginalThemeSpec
    AppTheme.Light -> LightThemeSpec
    AppTheme.Dark -> DarkThemeSpec
    AppTheme.Neon -> NeonThemeSpec
    AppTheme.Sepia -> SepiaThemeSpec
    AppTheme.Amoled -> AmoledThemeSpec
    AppTheme.AngelicSerenade -> AngelicSerenadeThemeSpec
    AppTheme.SayaNoUta -> SayaNoUtaThemeSpec
    AppTheme.HouseInFataMorgana -> HouseInFataMorganaThemeSpec
    AppTheme.Tsukihime -> TsukihimeThemeSpec
    AppTheme.MeikeiNoLupercalia -> MeikeiNoLupercaliaThemeSpec
    AppTheme.SteinsGate -> SteinsGateThemeSpec
    AppTheme.Ever17 -> Ever17ThemeSpec
    AppTheme.Clannad -> ClannadThemeSpec
    AppTheme.Higurashi -> HigurashiThemeSpec
    AppTheme.DokiDokiLiteratureClub -> DokiDokiLiteratureClubThemeSpec
    AppTheme.MuvLuvAlternative -> MuvLuvAlternativeThemeSpec
    AppTheme.Danganronpa -> DanganronpaThemeSpec
    AppTheme.WhiteAlbum2 -> WhiteAlbum2ThemeSpec
    AppTheme.Ef -> EfThemeSpec
    AppTheme.KaraNoShoujo -> KaraNoShoujoThemeSpec
    AppTheme.Umineko -> UminekoThemeSpec
    AppTheme.FateStayNight -> FateStayNightThemeSpec
}

/** The theme's display name, e.g. "Original" or "Meikei no Lupercalia". */
fun AppTheme.toLabel(): UiText = toSpec().name
