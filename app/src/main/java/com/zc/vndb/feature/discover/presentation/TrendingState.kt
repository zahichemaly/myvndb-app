package com.zc.vndb.feature.discover.presentation

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** The full "New & Trending" list, page by page. */
@Immutable
data class TrendingState(
    /** The first page is in flight. */
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    /** The first page failed. */
    val error: ErrorUIModel? = null,
    /** The New & Trending languages from Settings, e.g. "English, Japanese". */
    val languagesLabel: UiText? = null,
    val rows: ImmutableList<TrendingReleaseUIModel> = persistentListOf(),
    val isLoadingMore: Boolean = false,
    /** The last next-page request failed; the list shows a Retry instead of loading on scroll. */
    val loadMoreFailed: Boolean = false,
) {
    /** Loaded, and nothing came out in the user's languages. */
    val isEmpty: Boolean get() = !isLoading && error == null && rows.isEmpty()
}
