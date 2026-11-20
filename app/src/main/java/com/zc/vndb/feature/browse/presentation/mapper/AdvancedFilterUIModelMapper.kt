package com.zc.vndb.feature.browse.presentation.mapper

import com.zc.vndb.common.ui.mapper.languageName
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.ui.mapper.toNameLabel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.AdvancedFilterKind
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import com.zc.vndb.feature.browse.domain.model.SupportedLanguageChoices
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * One pill per filter that is applied: the age rating when it is not [defaultAgeRating], each
 * genre, then the language, length and era filters, in the order the sheet lists them.
 */
fun BrowseFilter.toActiveFilters(defaultAgeRating: AgeRating): ImmutableList<ActiveFilterUIModel> =
    buildList {
        if (ageRating != defaultAgeRating) {
            add(ActiveFilterUIModel(ActiveFilterKey.Age, ageRating.toLabel()))
        }
        genres.sortedBy { it.ordinal }.forEach {
            add(ActiveFilterUIModel(ActiveFilterKey.GenreFilter(it), it.toLabel()))
        }
        addAll(advanced.toActiveFilters())
    }.toImmutableList()

private fun AdvancedFilters.toActiveFilters(): List<ActiveFilterUIModel> = buildList {
    if (supportedLanguages.isNotEmpty()) {
        add(
            advancedPill(
                AdvancedFilterKind.SupportedLanguages,
                UiText.StringResource(
                    StringResource.browse_filter_chip_supported,
                    listOf(
                        supportedLanguages
                            .sortedBy { SupportedLanguageChoices.indexOf(it) }
                            .toLanguagesLabel(),
                    ),
                ),
            ),
        )
    }
    originalLanguage?.let {
        add(
            advancedPill(
                AdvancedFilterKind.OriginalLanguage,
                UiText.StringResource(
                    StringResource.browse_filter_chip_original,
                    listOf(languageName(it)),
                ),
            ),
        )
    }
    lengths.sortedBy { it.ordinal }.let { sorted ->
        when (sorted.size) {
            0 -> Unit

            1 -> add(advancedPill(AdvancedFilterKind.Length, sorted.single().toNameLabel()))

            else -> add(
                advancedPill(
                    AdvancedFilterKind.Length,
                    UiText.StringPlural(
                        PluralResource.browse_filter_chip_lengths,
                        sorted.size,
                        listOf(sorted.size),
                    ),
                ),
            )
        }
    }
    era?.let { add(advancedPill(AdvancedFilterKind.Era, it.toLabel())) }
}

private fun advancedPill(kind: AdvancedFilterKind, label: UiText) =
    ActiveFilterUIModel(ActiveFilterKey.Advanced(kind), label)

fun ReleaseEra.toLabel(): UiText = UiText.StringResource(
    when (this) {
        ReleaseEra.Since2020 -> StringResource.browse_era_since_2020
        ReleaseEra.In2010s -> StringResource.browse_era_2010s
        ReleaseEra.In2000s -> StringResource.browse_era_2000s
        ReleaseEra.Before2000 -> StringResource.browse_era_before_2000
    },
)
