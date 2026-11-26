package com.zc.vndb.feature.discover.data.mapper

import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.discover.data.dto.ReleaseDTO
import com.zc.vndb.feature.discover.data.dto.ReleaseLanguageDTO
import com.zc.vndb.feature.discover.data.dto.ReleaseVisualNovelDTO
import com.zc.vndb.feature.discover.domain.model.Platform
import com.zc.vndb.feature.discover.domain.model.ReleaseLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.YearMonth

class ReleaseDTOMapperTest {

    @Test
    fun `missing release fields map to safe defaults`() {
        val release = ReleaseDTO(
            id = "r1",
            released = null,
            patch = null,
            platforms = null,
            languages = null,
            vns = null,
        )
            .toDomain(SensitiveContentPolicy.Standard)

        assertEquals(ReleaseDate.Tba, release.releaseDate)
        assertFalse(release.isPatch)
        assertTrue(
            release.languages.isEmpty() && release.platforms.isEmpty() && release.novels.isEmpty(),
        )
    }

    @Test
    fun `maps languages, platforms and the novel`() {
        val release = ReleaseDTO(
            id = "r1",
            released = "2024-10",
            patch = true,
            platforms = listOf("win", "swi"),
            languages = listOf(
                ReleaseLanguageDTO("en", mtl = true),
                ReleaseLanguageDTO("ja", mtl = null),
            ),
            vns = listOf(
                ReleaseVisualNovelDTO(
                    id = "v1",
                    title = "ATRI",
                    olang = "ja",
                    released = "2020",
                    rating = 81.0,
                    image = null,
                ),
            ),
        ).toDomain(SensitiveContentPolicy.Standard)

        assertEquals(ReleaseDate.Month(YearMonth(2024, 10)), release.releaseDate)
        assertTrue(release.isPatch)
        assertEquals(listOf(Platform("win"), Platform("swi")), release.platforms)
        assertEquals(
            listOf(ReleaseLanguage("en", true), ReleaseLanguage("ja", false)),
            release.languages,
        )
        val novel = release.novels.single()
        assertEquals("ATRI", novel.title)
        assertEquals("ja", novel.originalLanguage)
        assertEquals(ReleaseDate.Year(2020), novel.releaseDate)
    }
}
