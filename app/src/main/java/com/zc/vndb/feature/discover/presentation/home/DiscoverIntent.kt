package com.zc.vndb.feature.discover.presentation.home

sealed interface DiscoverIntent {
    data object Load : DiscoverIntent
    data object Retry : DiscoverIntent

    /** Pull to refresh: reloads every section while the current ones stay on screen. */
    data object Refresh : DiscoverIntent
    data object RandomRollClicked : DiscoverIntent
    data class NovelClicked(val id: String) : DiscoverIntent

    /** A trending translation or patch: opens the release itself. */
    data class ReleaseClicked(val id: String) : DiscoverIntent
    data object SeeAllTopRatedClicked : DiscoverIntent
    data object SeeAllTrendingClicked : DiscoverIntent
    data object StatsClicked : DiscoverIntent
}
