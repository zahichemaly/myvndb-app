package com.zc.vndb.feature.browse.presentation.mapper

import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowseFilterUIModelMapperTest {

    @Test
    fun `relevance is offered only while searching`() {
        assertEquals(
            listOf(BrowseSort.Popularity, BrowseSort.Rating, BrowseSort.Newest, BrowseSort.Title),
            BrowseFilter().toSortOptions().map { it.sort },
        )
        assertEquals(
            BrowseSort.entries,
            BrowseFilter(query = "steins").toSortOptions().map { it.sort },
        )
        assertEquals(
            UiText.StringResource(StringResource.browse_sort_relevance),
            BrowseSort.Relevance.toLabel(),
        )
    }

    @Test
    fun `genres and age ratings have labels`() {
        assertEquals(
            UiText.StringResource(StringResource.browse_genre_scifi),
            Genre.SciFi.toLabel(),
        )
        assertEquals(
            UiText.StringResource(StringResource.browse_age_any),
            AgeRating.Any.toLabel(),
        )
        assertEquals(
            Genre.entries.size,
            Genre.entries.map { it.toLabel() }.toSet().size,
        )
    }

    @Test
    fun `result count is a grouped plural`() {
        assertEquals(
            UiText.StringPlural(
                PluralResource.browse_found_titles,
                35880,
                listOf("%,d".format(35880)),
            ),
            35880.toResultCountLabel(),
        )
    }
}
