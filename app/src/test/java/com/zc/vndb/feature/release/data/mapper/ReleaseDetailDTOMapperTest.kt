package com.zc.vndb.feature.release.data.mapper

import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.release.data.dto.ReleaseDetailDTO
import com.zc.vndb.feature.release.data.dto.ReleaseImageDTO
import com.zc.vndb.feature.release.data.dto.ReleaseNovelDTO
import com.zc.vndb.feature.release.domain.model.ReleaseImageType
import com.zc.vndb.feature.release.domain.model.ReleaseType
import com.zc.vndb.feature.release.domain.model.Resolution
import com.zc.vndb.feature.release.domain.model.Voicing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseDetailDTOMapperTest {

    private fun dto(
        resolution: List<Int>? = null,
        voiced: Int? = null,
        gtin: String? = null,
        images: List<ReleaseImageDTO>? = null,
        vns: List<ReleaseNovelDTO>? = null,
    ) = ReleaseDetailDTO(
        id = "r1",
        title = "Title",
        alttitle = "Title",
        released = null,
        minage = null,
        official = null,
        patch = null,
        freeware = null,
        uncensored = null,
        hasEro = null,
        platforms = null,
        media = null,
        resolution = resolution,
        engine = " ",
        voiced = voiced,
        catalog = null,
        gtin = gtin,
        notes = "",
        languages = null,
        vns = vns,
        producers = null,
        images = images,
        extlinks = null,
    )

    @Test
    fun `missing fields map to sensible defaults`() {
        val release = dto().toDomain(SensitiveContentPolicy.Standard)

        assertNull(release.altTitle)
        assertEquals(ReleaseDate.Tba, release.releaseDate)
        assertTrue(release.isOfficial)
        assertFalse(release.isPatch || release.isFreeware || release.hasAdultContent)
        assertNull(release.isUncensored)
        assertNull(release.engine)
        assertNull(release.notes)
        assertNull(release.resolution)
        assertNull(release.voicing)
        assertTrue(
            release.languages.isEmpty() && release.novels.isEmpty() && release.images.isEmpty(),
        )
    }

    @Test
    fun `resolution, voicing and barcode`() {
        assertEquals(
            Resolution(1920, 1080),
            dto(
                resolution = listOf(1920, 1080),
            ).toDomain(SensitiveContentPolicy.Standard).resolution,
        )
        assertNull(
            dto(resolution = listOf(0, 0)).toDomain(SensitiveContentPolicy.Standard).resolution,
        )
        assertEquals(
            listOf(Voicing.NotVoiced, Voicing.EroScenesOnly, Voicing.Partial, Voicing.Full, null),
            listOf(
                1,
                2,
                3,
                4,
                9,
            ).map { dto(voiced = it).toDomain(SensitiveContentPolicy.Standard).voicing },
        )
        assertNull(dto(gtin = "0").toDomain(SensitiveContentPolicy.Standard).gtin)
    }

    @Test
    fun `image types, and images without a url are dropped`() {
        val types = listOf("pkgfront", "pkgback", "pkgcontent", "pkgside", "pkgmed", "dig", "new")
        val images = dto(
            images = types.map {
                ReleaseImageDTO(
                    it,
                    "u-$it",
                    null,
                    1.5,
                    null,
                )
            } + ReleaseImageDTO("pkgfront", null, null, 0.0, 0.0),
        )
            .toDomain(SensitiveContentPolicy.Standard).images

        assertEquals(ReleaseImageType.entries, images.map { it.type })
        assertTrue(images.first().image.isSensitive)
    }

    @Test
    fun `VN release types`() {
        val novels = dto(
            vns = listOf(
                "complete",
                "partial",
                "trial",
                null,
            ).map { ReleaseNovelDTO("v1", null, it, null, null) },
        )
            .toDomain(SensitiveContentPolicy.Standard).novels

        assertEquals(
            listOf(
                ReleaseType.Complete,
                ReleaseType.Partial,
                ReleaseType.Trial,
                ReleaseType.Complete,
            ),
            novels.map { it.type },
        )
    }
}
