package com.zc.vndb.feature.discover.presentation

sealed interface TrendingIntent {
    data object Retry : TrendingIntent
    data object Refresh : TrendingIntent

    /** The list was scrolled near its end. */
    data object EndReached : TrendingIntent
    data object LoadMoreRetried : TrendingIntent
    data class NovelClicked(val id: String) : TrendingIntent
    data class ReleaseClicked(val id: String) : TrendingIntent
    data object BackClicked : TrendingIntent
}
