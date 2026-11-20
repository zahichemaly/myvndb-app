package com.zc.vndb.feature.browse.data.mapper

import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.feature.browse.data.dto.BrowseNovelDTO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BrowseNovelDTOMapperTest {

    private fun dto(
        title: String? = "STEINS;GATE",
        alttitle: String? = null,
        length: Int? = null,
        lengthMinutes: Int? = null,
        languages: List<String>? = null,
    ) = BrowseNovelDTO(
        id = "v2002",
        title = title,
        alttitle = alttitle,
        released = null,
        rating = null,
        length = length,
        lengthMinutes = lengthMinutes,
        image = null,
        developers = null,
        tags = null,
        languages = languages,
    )

    @Test
    fun `vote-based minutes map to VNDB length ranges`() {
        val expected = mapOf(
            119 to NovelLength.VeryShort,
            120 to NovelLength.Short,
            599 to NovelLength.Short,
            600 to NovelLength.Medium,
            1800 to NovelLength.Long,
            2999 to NovelLength.Long,
            3000 to NovelLength.VeryLong,
        )
        expected.forEach { (minutes, length) ->
            assertEquals(
                length,
                dto(lengthMinutes = minutes, length = 1).toDomain().length,
                "for $minutes min",
            )
        }
    }

    @Test
    fun `falls back to the 1-5 estimate, then to none`() {
        assertEquals(NovelLength.Long, dto(length = 4).toDomain().length)
        assertEquals(NovelLength.VeryShort, dto(length = 1).toDomain().length)
        assertNull(dto(length = 9).toDomain().length)
        assertNull(dto().toDomain().length)
    }

    @Test
    fun `original title is dropped when blank or identical`() {
        assertNull(dto(alttitle = " ").toDomain().originalTitle)
        assertNull(dto(alttitle = "STEINS;GATE").toDomain().originalTitle)
        assertEquals("シュタインズ・ゲート", dto(alttitle = "シュタインズ・ゲート").toDomain().originalTitle)
    }

    @Test
    fun `missing fields map to empty values`() {
        val novel = dto(title = null).toDomain()

        assertEquals("", novel.title)
        assertEquals(ReleaseDate.Tba, novel.releaseDate)
        assertNull(novel.cover)
        assertTrue(novel.developerNames.isEmpty() && novel.tags.isEmpty())
    }

    @Test
    fun `languages map through, and none is an empty list`() {
        assertEquals(listOf("ja", "en"), dto(languages = listOf("ja", "en")).toDomain().languages)
        assertEquals(emptyList(), dto(languages = null).toDomain().languages)
    }
}
