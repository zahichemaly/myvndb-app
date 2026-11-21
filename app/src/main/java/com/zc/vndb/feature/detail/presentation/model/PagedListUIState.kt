package com.zc.vndb.feature.detail.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** A list loaded page by page with "Load more". */
@Immutable
data class PagedListUIState<T>(
    val items: ImmutableList<T>,
    val hasMore: Boolean,
    val isLoadingMore: Boolean = false,
    val loadMoreFailed: Boolean = false,
)

/** The same paging state with its items mapped, e.g. domain items into UI models. */
inline fun <T, R> PagedListUIState<T>.mapItems(
    transform: (List<T>) -> List<R>,
): PagedListUIState<R> = PagedListUIState(
    items = transform(items).toImmutableList(),
    hasMore = hasMore,
    isLoadingMore = isLoadingMore,
    loadMoreFailed = loadMoreFailed,
)
