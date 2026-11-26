package com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.mapper

import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples
import kotlin.test.Test
import kotlin.test.assertEquals

class QuoteOfTheDayUIModelMapperTest {

    @Test
    fun `attributes the quote to its character and novel`() {
        val model = DiscoverSamples.quote().toUIModel(TitleLanguage.Romanized)

        assertEquals("People die when they are killed.", model.text)
        assertEquals("v11", model.novelId)
        assertEquals(
            UiText.StringResource(
                StringResource.discover_quote_attribution_character,
                listOf("Emiya Shirou", UiText.StringValue("Fate/stay night")),
            ),
            model.attribution,
        )
    }

    @Test
    fun `without a character only the novel is credited`() {
        val model = DiscoverSamples.quote(characterName = null).toUIModel(TitleLanguage.Romanized)

        assertEquals(
            UiText.StringResource(
                StringResource.discover_quote_attribution_novel,
                listOf(UiText.StringValue("Fate/stay night")),
            ),
            model.attribution,
        )
    }

    @Test
    fun `the novel title follows the title language setting`() {
        val quote = DiscoverSamples.quote().copy(novelOriginalTitle = "フェイト")
        val title = primaryTitle("Fate/stay night", "フェイト", TitleLanguage.Original)

        assertEquals(
            UiText.StringResource(
                StringResource.discover_quote_attribution_character,
                listOf("Emiya Shirou", title),
            ),
            quote.toUIModel(TitleLanguage.Original).attribution,
        )
    }
}
