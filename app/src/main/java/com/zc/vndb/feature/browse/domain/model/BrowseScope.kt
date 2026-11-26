package com.zc.vndb.feature.browse.domain.model

/**
 * What a Browse list is limited to: nothing (the Browse tab), one tag, producer or staff member,
 * or the top rated novels.
 */
sealed interface BrowseScope {
    data object None : BrowseScope
    data class Tag(val id: String) : BrowseScope
    data class Producer(val id: String) : BrowseScope
    data class Staff(val id: String) : BrowseScope

    /** Novels with enough votes for their rating to count (Discover's Top Rated, in full). */
    data object TopRated : BrowseScope

    /**
     * The sort a list starts with. A person's or studio's page opens on their latest work, Top
     * Rated on the best rated.
     */
    val defaultSort: BrowseSort
        get() = when (this) {
            is Producer, is Staff -> BrowseSort.Newest
            TopRated -> BrowseSort.Rating
            None, is Tag -> BrowseSort.Popularity
        }
}
