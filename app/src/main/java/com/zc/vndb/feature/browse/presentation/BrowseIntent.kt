package com.zc.vndb.feature.browse.presentation

import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.FilterDraft

sealed interface BrowseIntent {
    data object Load : BrowseIntent
    data object Retry : BrowseIntent

    /** Pull to refresh: reloads the first page (and tag header) while the results stay on screen. */
    data object Refresh : BrowseIntent
    data class QueryChanged(val query: String) : BrowseIntent
    data object SearchSubmitted : BrowseIntent
    data object QueryCleared : BrowseIntent
    data class SortSelected(val sort: BrowseSort) : BrowseIntent

    /** Reverses the current sort between ascending and descending. */
    data object SortDirectionToggled : BrowseIntent
    data object EndReached : BrowseIntent
    data object LoadMoreRetried : BrowseIntent

    /** The filter icon: opens the sheet with the filters that are on. */
    data object FiltersOpened : BrowseIntent

    /** An edit in the sheet; nothing is applied until [FiltersApplied]. */
    data class FilterDraftChanged(val draft: FilterDraft) : BrowseIntent

    /** The sheet's Reset: the draft goes back to the starting filters. */
    data object FiltersReset : BrowseIntent
    data object FiltersApplied : BrowseIntent
    data object FiltersDismissed : BrowseIntent
    data class ActiveFilterRemoved(val key: ActiveFilterKey) : BrowseIntent
    data object ActiveFiltersCleared : BrowseIntent
    data class NovelClicked(val id: String) : BrowseIntent
    data object BackClicked : BrowseIntent
    data object DescriptionToggled : BrowseIntent
    data class LinkClicked(val url: String) : BrowseIntent
}
