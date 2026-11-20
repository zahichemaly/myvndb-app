package com.zc.vndb.feature.browse.domain.model

data class BrowseFilter(
    val query: String = "",
    val ageRating: AgeRating = AgeRating.AllAges,
    val genres: Set<Genre> = emptySet(),
    /** The language, length, era and rating filters from the filter sheet. */
    val advanced: AdvancedFilters = AdvancedFilters(),
    val sort: BrowseSort = BrowseSort.Popularity,
    /** The sort runs against its natural direction (see [BrowseSort.isNaturallyDescending]). */
    val isReversed: Boolean = false,
    /** A tag or producer page limits the list to it, on top of [genres]. */
    val scope: BrowseScope = BrowseScope.None,
)

/** The order the results come in: the sort's natural direction, flipped when [BrowseFilter.isReversed]. */
val BrowseFilter.isDescending: Boolean get() = sort.isNaturallyDescending != isReversed
