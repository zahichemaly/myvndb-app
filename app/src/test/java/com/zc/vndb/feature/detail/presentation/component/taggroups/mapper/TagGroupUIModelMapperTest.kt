package com.zc.vndb.feature.detail.presentation.component.taggroups.mapper

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.DetailSamples.novel
import com.zc.vndb.feature.detail.domain.model.DetailSamples.tag
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteTone
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TagGroupUIModelMapperTest {

    private val detail = novel(
        tags = listOf(
            tag("Time Travel", 3.0),
            tag("Minor", 2.0, spoiler = 1),
            tag("Major", 2.0, spoiler = 2),
            tag("ADV", 2.0, category = TagCategory.Technical),
            tag("Sexual", 3.0, category = TagCategory.Sexual),
        ),
    )

    @Test
    fun `spoiler level controls which tags show and how many are hidden`() {
        val none = detail.toTagGroups(SpoilerLevel.None)
        assertEquals(
            listOf(TagChipUIModel("g-Time Travel", UiText.StringValue("Time Travel"))),
            none[0].tags,
        )
        assertEquals(listOf(TagChipUIModel("g-ADV", UiText.StringValue("ADV"))), none[1].tags)
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_spoiler, 2, listOf(2)),
            detail.toHiddenTagsLabel(SpoilerLevel.None),
        )

        assertEquals(2, detail.toTagGroups(SpoilerLevel.Minor)[0].tags.size)
        assertEquals(3, detail.toTagGroups(SpoilerLevel.Major)[0].tags.size)
        assertNull(detail.toHiddenTagsLabel(SpoilerLevel.Major))
    }

    @Test
    fun `sexual tags are never listed`() {
        val all = detail.toTagGroups(SpoilerLevel.Major).flatMap { it.tags }.map { it.label }
        assertEquals(false, UiText.StringValue("Sexual") in all)
    }

    @Test
    fun `content notes come from sexual tags and the cover`() {
        assertEquals(listOf(ContentNoteTone.Adult), detail.toContentNotes().map { it.tone })

        val violent = novel(
            cover = Cover(url = "u", thumbnailUrl = null, sexual = 0.0, violence = 1.5),
        )
        assertEquals(
            listOf(ContentNoteTone.Safe, ContentNoteTone.Caution),
            violent.toContentNotes().map { it.tone },
        )
    }

    @Test
    fun `hidden count only includes tags the groups would actually show`() {
        val crowded = novel(
            tags = (1..20).map {
                tag(
                    "Safe $it",
                    2.0,
                )
            } + (1..30).map { tag("Spoiler $it", 3.0, spoiler = 2) },
        )

        // Themes hold 12: at Major all 12 slots go to the higher-rated spoiler tags, not 30.
        assertEquals(
            UiText.StringPlural(PluralResource.detail_hidden_by_spoiler, 12, listOf(12)),
            crowded.toHiddenTagsLabel(SpoilerLevel.None),
        )
        assertNull(crowded.toHiddenTagsLabel(SpoilerLevel.Major))
    }
}
