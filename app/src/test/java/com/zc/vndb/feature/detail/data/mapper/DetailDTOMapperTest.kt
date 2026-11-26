package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.detail.data.dto.CharacterDTO
import com.zc.vndb.feature.detail.data.dto.CharacterNovelDTO
import com.zc.vndb.feature.detail.data.dto.EditionDTO
import com.zc.vndb.feature.detail.data.dto.RelationDTO
import com.zc.vndb.feature.detail.domain.model.RelationType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class DetailDTOMapperTest {

    private fun relation(code: String?) = RelationDTO(
        "v1",
        "T",
        code,
        relationOfficial = null,
        released = null,
        rating = null,
    )

    @Test
    fun `relation codes map to types and unknown ones to Other`() {
        val expected = mapOf(
            "seq" to RelationType.Sequel,
            "preq" to RelationType.Prequel,
            "set" to RelationType.SameSetting,
            "alt" to RelationType.AlternativeVersion,
            "char" to RelationType.SharesCharacters,
            "side" to RelationType.SideStory,
            "par" to RelationType.ParentStory,
            "ser" to RelationType.SameSeries,
            "fan" to RelationType.FanDisc,
            "orig" to RelationType.OriginalGame,
            "new" to RelationType.Other,
            null to RelationType.Other,
        )
        expected.forEach { (code, type) ->
            assertEquals(
                type,
                relation(code).toDomain().relation,
                "for $code",
            )
        }
        assertFalse(relation("seq").toDomain().isOfficial)
    }

    @Test
    fun `characters use their appearance in the given novel`() {
        val dto = CharacterDTO(
            id = "c1",
            name = "Okabe",
            original = "Okabe",
            image = null,
            vns = listOf(CharacterNovelDTO("v1", "side", 0), CharacterNovelDTO("v2", "main", null)),
        )

        val inV2 = dto.toDomain("v2", SensitiveContentPolicy.Standard)
        assertEquals(CharacterRole.Main, inV2?.role)
        assertEquals(2, inV2?.spoilerLevel)
        assertNull(inV2?.originalName)
        assertEquals(CharacterRole.Side, dto.toDomain("v1", SensitiveContentPolicy.Standard)?.role)
        assertNull(dto.toDomain("v3", SensitiveContentPolicy.Standard))
    }

    @Test
    fun `editions split by platform and group into families`() {
        val dto = EditionDTO(
            id = "r1",
            title = null,
            released = "2016",
            platforms = listOf("win", "swi", "and", "web", "xyz"),
            languages = null,
            official = null,
            patch = null,
            minage = null,
        )

        val editions = dto.toDomain()

        assertEquals(
            listOf(
                PlatformFamily.Pc,
                PlatformFamily.Console,
                PlatformFamily.Mobile,
                PlatformFamily.Web,
                PlatformFamily.Other,
            ),
            editions.map { it.family },
        )
        assertFalse(editions.first().isOfficial)
        assertEquals(1, dto.copy(platforms = null).toDomain().size)
    }
}
