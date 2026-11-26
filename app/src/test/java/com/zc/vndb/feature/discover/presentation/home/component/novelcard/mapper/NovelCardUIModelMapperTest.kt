package com.zc.vndb.feature.discover.presentation.home.component.novelcard.mapper

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NovelCardUIModelMapperTest {

    private val cover = Cover(url = "full", thumbnailUrl = "thumb", sexual = 0.0, violence = 0.0)
    private val novel = DiscoverNovel(
        id = "v1",
        title = "CLANNAD",
        rating = 88.4,
        cover = cover,
        developerName = "Key",
    )

    @Test
    fun `maps display values and prefers the thumbnail`() {
        val card = novel.toNovelCardUIModel()

        assertEquals(UiText.StringValue("CLANNAD"), card.title)
        assertEquals(UiText.StringValue("Key"), card.subtitle)
        assertEquals(UiText.StringValue("8.84"), card.ratingLabel)
        assertEquals("thumb", card.coverUrl)
    }

    @Test
    fun `falls back to the full cover, and tolerates missing values`() {
        assertEquals(
            "full",
            novel.copy(cover = cover.copy(thumbnailUrl = null)).toNovelCardUIModel().coverUrl,
        )

        val bare = novel.copy(
            rating = null,
            cover = null,
            developerName = null,
        ).toNovelCardUIModel()
        assertNull(bare.ratingLabel)
        assertNull(bare.coverUrl)
        assertNull(bare.subtitle)
    }
}
