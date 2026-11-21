package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.AppTheme
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
import com.zc.vndb.common.ui.theme.WhiteAlbum2ThemeSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AppThemeUIModelMapperTest {

    @Test
    fun `every theme maps to its own spec`() {
        assertEquals(OriginalThemeSpec, AppTheme.Original.toSpec())
        assertEquals(LightThemeSpec, AppTheme.Light.toSpec())
        assertEquals(DarkThemeSpec, AppTheme.Dark.toSpec())
        assertEquals(NeonThemeSpec, AppTheme.Neon.toSpec())
        assertEquals(SepiaThemeSpec, AppTheme.Sepia.toSpec())
        assertEquals(AmoledThemeSpec, AppTheme.Amoled.toSpec())
        assertEquals(AngelicSerenadeThemeSpec, AppTheme.AngelicSerenade.toSpec())
        assertEquals(SayaNoUtaThemeSpec, AppTheme.SayaNoUta.toSpec())
        assertEquals(HouseInFataMorganaThemeSpec, AppTheme.HouseInFataMorgana.toSpec())
        assertEquals(TsukihimeThemeSpec, AppTheme.Tsukihime.toSpec())
        assertEquals(MeikeiNoLupercaliaThemeSpec, AppTheme.MeikeiNoLupercalia.toSpec())
        assertEquals(SteinsGateThemeSpec, AppTheme.SteinsGate.toSpec())
        assertEquals(Ever17ThemeSpec, AppTheme.Ever17.toSpec())
        assertEquals(ClannadThemeSpec, AppTheme.Clannad.toSpec())
        assertEquals(HigurashiThemeSpec, AppTheme.Higurashi.toSpec())
        assertEquals(DokiDokiLiteratureClubThemeSpec, AppTheme.DokiDokiLiteratureClub.toSpec())
        assertEquals(MuvLuvAlternativeThemeSpec, AppTheme.MuvLuvAlternative.toSpec())
        assertEquals(DanganronpaThemeSpec, AppTheme.Danganronpa.toSpec())
        assertEquals(WhiteAlbum2ThemeSpec, AppTheme.WhiteAlbum2.toSpec())
        assertEquals(EfThemeSpec, AppTheme.Ef.toSpec())
        assertEquals(KaraNoShoujoThemeSpec, AppTheme.KaraNoShoujo.toSpec())
        assertEquals(UminekoThemeSpec, AppTheme.Umineko.toSpec())
        assertEquals(FateStayNightThemeSpec, AppTheme.FateStayNight.toSpec())
    }

    @Test
    fun `every theme has a distinct name and a distinct look`() {
        val specs = AppTheme.entries.map { it.toSpec() }

        assertEquals(AppTheme.entries.size, specs.map { it.name }.toSet().size)
        val looks = specs.map { Pair(it.colorScheme, it.wallpaper) }
        assertEquals(AppTheme.entries.size, looks.toSet().size)
        assertEquals(specs.map { it.name }, AppTheme.entries.map { it.toLabel() })
    }

    @Test
    fun `the plain themes have no wallpaper and the other themes have one`() {
        val plain = listOf(
            AppTheme.Original,
            AppTheme.Light,
            AppTheme.Dark,
            AppTheme.Neon,
            AppTheme.Sepia,
            AppTheme.Amoled,
        )
        plain.forEach { assertNull(it.toSpec().wallpaper, "$it should have no wallpaper") }
        (AppTheme.entries - plain.toSet()).forEach {
            assertNotNull(it.toSpec().wallpaper, "$it needs a wallpaper")
        }
    }

    @Test
    fun `the plain themes follow Original in the list`() {
        assertEquals(
            listOf(
                AppTheme.Original,
                AppTheme.Light,
                AppTheme.Dark,
                AppTheme.Neon,
                AppTheme.Sepia,
                AppTheme.Amoled,
            ),
            AppTheme.entries.take(6),
        )
    }

    @Test
    fun `Original and Angelic Serenade share the Nocturne palette`() {
        assertEquals(OriginalThemeSpec.colors, AngelicSerenadeThemeSpec.colors)
        assertEquals(OriginalThemeSpec.colorScheme, AngelicSerenadeThemeSpec.colorScheme)
    }

    @Test
    fun `Saya no Uta has its own palette`() {
        assertNotEquals(OriginalThemeSpec.colorScheme, SayaNoUtaThemeSpec.colorScheme)
        assertNotEquals(OriginalThemeSpec.colors, SayaNoUtaThemeSpec.colors)
    }
}
