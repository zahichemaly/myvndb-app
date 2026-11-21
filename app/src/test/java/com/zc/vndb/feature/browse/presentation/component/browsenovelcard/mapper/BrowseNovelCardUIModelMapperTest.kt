package com.zc.vndb.feature.browse.presentation.component.browsenovelcard.mapper

import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.ui.mapper.toNameWithRangeLabel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.BrowseSamples
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.YearMonth

class BrowseNovelCardUIModelMapperTest {

    private val novel = BrowseSamples.novel(
        "v2002",
        tags = listOf(BrowseSamples.tag("Time Travel")),
    )

    @Test
    fun `maps display values`() {
        val card = novel.toCardUIModel()

        assertEquals(UiText.StringValue("Novel v2002"), card.title)
        assertEquals(UiText.StringValue("MAGES."), card.creditsLabel)
        assertEquals(UiText.StringValue("2009"), card.yearLabel)
        assertEquals(UiText.StringValue("9.02"), card.ratingLabel)
        assertEquals(NovelLength.Long.toNameWithRangeLabel(), card.lengthLabel)
        assertEquals("thumb", card.coverUrl)
        assertEquals(listOf<UiText>(UiText.StringValue("Time Travel")), card.tags)
    }

    @Test
    fun `a card shows at most two tags`() {
        val card = novel.copy(
            tags = listOf("Mystery", "Time Travel", "Sci-Fi").map { BrowseSamples.tag(it) },
        ).toCardUIModel()

        assertEquals(
            listOf<UiText>(UiText.StringValue("Mystery"), UiText.StringValue("Time Travel")),
            card.tags,
        )
    }

    @Test
    fun `credits show the first two developers`() {
        val card = novel.copy(developerNames = listOf("5pb.", "Nitroplus", "Third")).toCardUIModel()

        assertEquals(
            UiText.StringResource(StringResource.credits_separator, listOf("5pb.", "Nitroplus")),
            card.creditsLabel,
        )
        assertNull(novel.copy(developerNames = emptyList()).toCardUIModel().creditsLabel)
    }

    @Test
    fun `year comes from partial dates, TBA otherwise`() {
        assertEquals(
            UiText.StringValue("2021"),
            novel.copy(
                releaseDate = ReleaseDate.Month(YearMonth(2021, 3)),
            ).toCardUIModel().yearLabel,
        )
        assertEquals(
            UiText.StringValue("2020"),
            novel.copy(releaseDate = ReleaseDate.Year(2020)).toCardUIModel().yearLabel,
        )
        assertEquals(
            UiText.StringResource(StringResource.release_tba),
            novel.copy(releaseDate = ReleaseDate.Tba).toCardUIModel().yearLabel,
        )
    }

    @Test
    fun `every length has a label and missing values stay null`() {
        val labels = NovelLength.entries.map { novel.copy(length = it).toCardUIModel().lengthLabel }
        assertEquals(
            NovelLength.entries.map { it.toNameWithRangeLabel() },
            labels,
        )
        val bare = novel.copy(length = null, rating = null, cover = null).toCardUIModel()
        assertNull(bare.lengthLabel)
        assertNull(bare.ratingLabel)
        assertNull(bare.coverUrl)
    }

    @Test
    fun `pills for the chosen languages the novel is available in`() {
        val chosen = listOf(LanguageOption("en", "English"), LanguageOption("de", "German"))

        val card = novel.copy(languages = listOf("ja", "en", "ko"))
            .toCardUIModel(cardLanguages = chosen)

        assertEquals(listOf("en"), card.languagePills.map { it.code })
        assertEquals(listOf("ja", "en", "ko"), card.languageCodes)
    }

    @Test
    fun `no pills when no language is chosen`() {
        val card = novel.copy(languages = listOf("ja", "en")).toCardUIModel()

        assertTrue(card.languagePills.isEmpty())
        assertEquals(listOf("ja", "en"), card.languageCodes)
    }
}
