package com.zc.vndb.feature.browse.domain.usecase

import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseNovel
import com.zc.vndb.feature.browse.domain.model.BrowsePage
import com.zc.vndb.feature.browse.domain.repository.BrowseRepository
import org.koin.core.annotation.Factory

/**
 * One page of Browse results. Normalises the filter (trimmed query; Relevance needs a query,
 * otherwise Popularity), asks for the total on the first page only, and keeps each novel's
 * three most relevant spoiler-free content tags.
 */
@Factory
class SearchNovelsUseCase(private val repository: BrowseRepository) {

    suspend operator fun invoke(filter: BrowseFilter, page: Int): Result<BrowsePage> =
        repository.searchNovels(
            filter = filter.normalized(),
            page = page,
            pageSize = PAGE_SIZE,
            withCount = page == FIRST_PAGE,
        ).map { result -> result.copy(novels = result.novels.map { it.withCardTags() }) }

    private fun BrowseNovel.withCardTags(): BrowseNovel = copy(
        tags = tags
            .filter { it.category == TagCategory.Content && it.spoilerLevel == 0 }
            .sortedByDescending { it.rating }
            .take(MAX_TAGS),
    )

    companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 20
        private const val MAX_TAGS = 3
    }
}
