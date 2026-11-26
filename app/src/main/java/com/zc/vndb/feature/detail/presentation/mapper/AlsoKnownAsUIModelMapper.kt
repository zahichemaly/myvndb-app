package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.ui.mapper.languageFlag
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.presentation.model.AlsoKnownAsUIModel
import com.zc.vndb.feature.detail.presentation.model.TitleLineUIModel
import java.util.Locale
import kotlinx.collections.immutable.toImmutableList

/** Titles in other languages (except those the hero already shows) and aliases; null when none. */
fun NovelDetail.toAlsoKnownAs(): AlsoKnownAsUIModel? {
    val shown = setOf(title, originalTitle)
    val titleLines = titles.filter { it.title !in shown }.map {
        TitleLineUIModel(
            language = UiText.StringValue(languageLabel(it.languageCode)),
            title = UiText.StringValue(it.title),
            latin = it.latin?.let(UiText::StringValue),
        )
    }
    if (titleLines.isEmpty() && aliases.isEmpty()) return null
    return AlsoKnownAsUIModel(
        titles = titleLines.toImmutableList(),
        aliases = aliases.map(UiText::StringValue).toImmutableList(),
    )
}

/** "🇰🇷 Korean", or just "Catalan" for a language without a national flag. */
private fun languageLabel(code: String): String {
    val name = Locale.forLanguageTag(code).displayLanguage.ifBlank { code }
    return languageFlag(code)?.let { "$it $name" } ?: name
}
