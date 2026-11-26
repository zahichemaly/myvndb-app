package com.zc.vndb.feature.discover.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.feature.discover.presentation.DiscoverRoute
import com.zc.vndb.feature.discover.presentation.TrendingRoute

fun EntryProviderScope<NavKey>.discoverEntry(
    onOpenNovel: (id: String) -> Unit,
    onOpenRelease: (id: String) -> Unit,
    onOpenBrowse: () -> Unit,
    onOpenTopRatedPage: () -> Unit,
    onOpenTrendingPage: () -> Unit,
) {
    entry<DiscoverNavKey> {
        DiscoverRoute(
            onOpenNovel = onOpenNovel,
            onOpenRelease = onOpenRelease,
            onOpenBrowse = onOpenBrowse,
            onOpenTopRatedPage = onOpenTopRatedPage,
            onOpenTrendingPage = onOpenTrendingPage,
        )
    }
}

/** The full "New & Trending" list, opened from Discover's See all. */
fun EntryProviderScope<NavKey>.trendingEntry(
    onNavigateBack: () -> Unit,
    onOpenNovel: (id: String) -> Unit,
    onOpenRelease: (id: String) -> Unit,
) {
    entry<TrendingNavKey> {
        TrendingRoute(
            onNavigateBack = onNavigateBack,
            onOpenNovel = onOpenNovel,
            onOpenRelease = onOpenRelease,
        )
    }
}

/** Opens the full "New & Trending" list. */
fun Navigator.navigateToTrending() = navigate(TrendingNavKey)
