package com.zc.vndb.feature.browse.presentation.mapper

import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.presentation.model.SortOptionUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** Sort choices for the menu; Relevance only makes sense while searching. */
fun BrowseFilter.toSortOptions(): ImmutableList<SortOptionUIModel> = BrowseSort.entries
    .filter { it != BrowseSort.Relevance || query.isNotBlank() }
    .map { SortOptionUIModel(sort = it, label = it.toLabel()) }
    .toImmutableList()

fun BrowseSort.toLabel(): UiText = UiText.StringResource(
    when (this) {
        BrowseSort.Relevance -> StringResource.browse_sort_relevance
        BrowseSort.Popularity -> StringResource.browse_sort_popularity
        BrowseSort.Rating -> StringResource.browse_sort_rating
        BrowseSort.Newest -> StringResource.browse_sort_newest
        BrowseSort.Title -> StringResource.browse_sort_title
    },
)

fun AgeRating.toLabel(): UiText = UiText.StringResource(
    when (this) {
        AgeRating.AllAges -> StringResource.browse_age_all
        AgeRating.Adult -> StringResource.browse_age_adult
        AgeRating.Any -> StringResource.browse_age_any
    },
)

/** "Found 142 titles", grouped for the device locale. */
fun Int.toResultCountLabel(): UiText = UiText.StringPlural(
    PluralResource.browse_found_titles,
    this,
    listOf("%,d".format(this)),
)

fun Genre.toLabel(): UiText = UiText.StringResource(
    when (this) {
        Genre.Romance -> StringResource.browse_genre_romance
        Genre.Mystery -> StringResource.browse_genre_mystery
        Genre.SciFi -> StringResource.browse_genre_scifi
        Genre.Horror -> StringResource.browse_genre_horror
        Genre.Psychological -> StringResource.browse_genre_psychological
        Genre.Fantasy -> StringResource.browse_genre_fantasy
        Genre.Drama -> StringResource.browse_genre_drama
        Genre.Comedy -> StringResource.browse_genre_comedy
        Genre.Action -> StringResource.browse_genre_action
    },
)
