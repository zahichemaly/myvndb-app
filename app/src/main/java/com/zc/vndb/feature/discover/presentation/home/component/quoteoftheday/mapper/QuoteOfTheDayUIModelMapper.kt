package com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.mapper

import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.QuoteOfTheDay
import com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.model.QuoteOfTheDayUIModel

fun QuoteOfTheDay.toUIModel(titleLanguage: TitleLanguage): QuoteOfTheDayUIModel {
    val title = primaryTitle(novelTitle, novelOriginalTitle, titleLanguage)
    return QuoteOfTheDayUIModel(
        text = text,
        attribution = if (characterName == null) {
            UiText.StringResource(StringResource.discover_quote_attribution_novel, listOf(title))
        } else {
            UiText.StringResource(
                StringResource.discover_quote_attribution_character,
                listOf(characterName, title),
            )
        },
        novelId = novelId,
    )
}
