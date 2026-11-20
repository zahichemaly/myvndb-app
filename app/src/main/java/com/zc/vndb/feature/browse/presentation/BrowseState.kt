package com.zc.vndb.feature.browse.presentation

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.presentation.component.browsenovelcard.model.BrowseNovelCardUIModel
import com.zc.vndb.feature.browse.presentation.component.scopeheader.model.ScopeHeaderUIModel
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterUIModel
import com.zc.vndb.feature.browse.presentation.model.FilterSheetUIState
import com.zc.vndb.feature.browse.presentation.model.SortOptionUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class BrowseState(
    /** A tag page (opened from a VN) rather than the Browse tab: shows Back and the tag header. */
    val isScopedPage: Boolean = false,
    val header: ScopeHeaderUIModel? = null,
    val isDescriptionExpanded: Boolean = false,
    /** What is typed in the search field (applied after a short pause). */
    val query: String = "",
    val sort: BrowseSort = BrowseSort.Popularity,
    /** Up arrow on the direction button; Popularity starts with the highest, so it is descending. */
    val isSortAscending: Boolean = false,
    val sortOptions: ImmutableList<SortOptionUIModel> = persistentListOf(),
    /** The filters that are applied, as removable pills; none means nothing is filtered. */
    val activeFilters: ImmutableList<ActiveFilterUIModel> = persistentListOf(),
    /** Set while the filter sheet is open. */
    val filterSheet: FilterSheetUIState? = null,
    val results: ImmutableList<BrowseNovelCardUIModel> = persistentListOf(),
    val resultCountLabel: UiText? = null,
    /** First page in flight; results are replaced when it lands. */
    val isLoading: Boolean = false,
    /** Pull to refresh in flight; the current results stay on screen. */
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val loadMoreFailed: Boolean = false,
    /** First page failed. */
    val error: ErrorUIModel? = null,
) {
    val showEmpty: Boolean get() = !isLoading && error == null && results.isEmpty()

    /** A page request is in flight, so no other one should start. */
    val isBusy: Boolean get() = isLoading || isRefreshing || isLoadingMore
}
