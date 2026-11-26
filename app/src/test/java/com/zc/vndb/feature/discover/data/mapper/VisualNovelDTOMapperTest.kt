package com.zc.vndb.feature.discover.data.mapper

import com.zc.vndb.common.data.dto.DeveloperDTO
import com.zc.vndb.common.data.dto.ImageDTO
import com.zc.vndb.common.data.dto.TagDTO
import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.NovelTag
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.feature.discover.data.dto.StaffDTO
import com.zc.vndb.feature.discover.data.dto.VisualNovelDTO
import com.zc.vndb.feature.discover.domain.model.StaffRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VisualNovelDTOMapperTest {

    private fun dto(
        id: String = "v1",
        image: ImageDTO? = null,
        description: String? = null,
        developers: List<DeveloperDTO>? = null,
        staff: List<StaffDTO>? = null,
        tags: List<TagDTO>? = null,
    ) = VisualNovelDTO(
        id = id,
        title = null,
        rating = null,
        description = description,
        image = image,
        developers = developers,
        staff = staff,
        tags = tags,
    )

    @Test
    fun `missing optional fields map to empty values`() {
        val details = dto().toNovelDetails(SensitiveContentPolicy.Standard)

        assertEquals("", details.title)
        assertNull(details.rating)
        assertNull(details.cover)
        assertNull(details.description)
        assertTrue(
            details.developerNames.isEmpty() && details.staff.isEmpty() && details.tags.isEmpty(),
        )
        assertNull(dto().toDiscoverNovel(SensitiveContentPolicy.Standard).developerName)
    }

    @Test
    fun `blank descriptions become null`() {
        assertNull(
            dto(description = "  ").toNovelDetails(SensitiveContentPolicy.Standard).description,
        )
    }

    @Test
    fun `image without url is no cover and missing flags count as safe`() {
        assertNull(
            dto(
                image = ImageDTO(url = null, thumbnail = "t", sexual = 2.0, violence = 0.0),
            ).toDiscoverNovel(SensitiveContentPolicy.Standard).cover,
        )
        assertEquals(
            Cover(url = "u", thumbnailUrl = null, sexual = 0.0, violence = 0.0),
            dto(
                image = ImageDTO(url = "u", thumbnail = null, sexual = null, violence = null),
            ).toDiscoverNovel(SensitiveContentPolicy.Standard).cover,
        )
    }

    @Test
    fun `staff roles map to known roles or Other`() {
        val roles = dto(
            staff = listOf("scenario", "chardesign", "art", "music", "director", "translator", null)
                .map { StaffDTO(name = "n", role = it) },
        ).toNovelDetails(SensitiveContentPolicy.Standard).staff.map { it.role }

        assertEquals(
            listOf(
                StaffRole.Scenario,
                StaffRole.CharacterDesign,
                StaffRole.Art,
                StaffRole.Music,
                StaffRole.Director,
                StaffRole.Other,
                StaffRole.Other,
            ),
            roles,
        )
    }

    @Test
    fun `tags map categories, drop unknown ones and hide unknown spoilers`() {
        val tags = dto(
            tags = listOf(
                TagDTO(
                    id = "g-Fantasy",
                    name = "Fantasy",
                    rating = 2.5,
                    spoiler = 0,
                    category = "cont",
                ),
                TagDTO(
                    id = "g-Sexual",
                    name = "Sexual",
                    rating = null,
                    spoiler = null,
                    category = "ero",
                ),
                TagDTO(id = "g-ADV", name = "ADV", rating = 1.0, spoiler = 0, category = "tech"),
                TagDTO(
                    id = "g-Mystery",
                    name = "Mystery",
                    rating = 1.0,
                    spoiler = 0,
                    category = "new",
                ),
            ),
        ).toNovelDetails(SensitiveContentPolicy.Standard).tags

        assertEquals(
            listOf(
                NovelTag("g-Fantasy", "Fantasy", 2.5, 0, TagCategory.Content),
                NovelTag("g-Sexual", "Sexual", 0.0, 2, TagCategory.Sexual),
                NovelTag("g-ADV", "ADV", 1.0, 0, TagCategory.Technical),
            ),
            tags,
        )
    }

    @Test
    fun `id number strips the v prefix`() {
        assertEquals(70743, dto(id = "v70743").idNumber())
        assertFailsWith<DomainError.InvalidData> { dto(id = "r12").idNumber() }
    }
}
