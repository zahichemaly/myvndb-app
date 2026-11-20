package com.zc.vndb.feature.browse.domain.usecase

import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseSort

/**
 * The filter as it is sent: a trimmed query, and Relevance (which needs a query) falling back to
 * the scope's default sort. A fallback starts in that sort's own direction.
 */
internal fun BrowseFilter.normalized(): BrowseFilter {
    val trimmed = query.trim()
    val effectiveSort = if (sort == BrowseSort.Relevance && trimmed.isEmpty()) {
        scope.defaultSort
    } else {
        sort
    }
    return copy(
        query = trimmed,
        sort = effectiveSort,
        isReversed = isReversed && effectiveSort == sort,
    )
}
