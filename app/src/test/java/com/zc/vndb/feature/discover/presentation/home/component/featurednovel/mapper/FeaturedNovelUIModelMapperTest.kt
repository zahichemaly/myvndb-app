package com.zc.vndb.feature.discover.presentation.home.component.featurednovel.mapper

import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.FeaturedNovel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeaturedNovelUIModelMapperTest {

    private val featured = FeaturedNovel(
        id = "v11",
        title = "Fate/stay night",
        rating = 89.2,
        cover = Cover(
            url = "https://t.vndb.org/cv/1.jpg",
            thumbnailUrl = "t",
            sexual = 0.0,
            violence = 1.2,
        ),
        description = "Hello",
        developerName = "TYPE-MOON",
        scenarioWriter = "Nasu Kinoko",
        tags = listOf("Urban Fantasy", "Kinetic Novel"),
        isAllAges = true,
    )

    @Test
    fun `maps display values`() {
        val model = featured.toUIModel()

        assertEquals(UiText.StringValue("Fate/stay night"), model.title)
        assertEquals(
            UiText.StringResource(StringResource.rating_out_of_ten, listOf("8.92")),
            model.ratingLabel,
        )
        assertEquals(UiText.StringValue("TYPE-MOON"), model.developerLabel)
        assertEquals(UiText.StringValue("Nasu Kinoko"), model.writerLabel)
        assertEquals("https://t.vndb.org/cv/1.jpg", model.coverUrl)
        assertTrue(model.isCoverSensitive)
        assertEquals(
            listOf<UiText>(
                UiText.StringValue("Urban Fantasy"),
                UiText.StringValue("Kinetic Novel"),
            ),
            model.tags,
        )
        assertEquals(UiText.StringResource(StringResource.discover_all_ages), model.ageLabel)
    }

    @Test
    fun `missing values map to null`() {
        val model = featured.copy(
            rating = null,
            cover = null,
            description = null,
            developerName = null,
            scenarioWriter = null,
            isAllAges = false,
        ).toUIModel()

        assertNull(model.ratingLabel)
        assertNull(model.coverUrl)
        assertFalse(model.isCoverSensitive)
        assertNull(model.description)
        assertNull(model.developerLabel)
        assertNull(model.writerLabel)
        assertNull(model.ageLabel)
    }

    @Test
    fun `description BBCode becomes one line of plain text`() {
        val raw = "[b]-Decide The Fate-[/b]\nCAN YOU?\n\n" +
            "With [url=/c735]Rance[/url] and [spoiler]the twist[/spoiler]friends."

        assertEquals(
            UiText.StringValue("-Decide The Fate- CAN YOU? With Rance and friends."),
            featured.copy(description = raw).toUIModel().description,
        )
    }

    @Test
    fun `a description that is only markup is dropped`() {
        assertNull(
            featured.copy(description = "[spoiler]all of it[/spoiler]").toUIModel().description,
        )
    }
}
