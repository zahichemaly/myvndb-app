package com.zc.vndb.feature.browse.presentation.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.Genre

/**
 * What the filter sheet is editing: the age rating, genres and the other filters. Nothing is
 * applied until the user taps Apply, so closing the sheet drops it.
 */
@Immutable
data class FilterDraft(
    val ageRating: AgeRating,
    val genres: Set<Genre> = emptySet(),
    val advanced: AdvancedFilters = AdvancedFilters(),
) {
    companion object {
        fun of(filter: BrowseFilter) = FilterDraft(
            ageRating = filter.ageRating,
            genres = filter.genres,
            advanced = filter.advanced,
        )
    }
}
