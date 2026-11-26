package com.zc.vndb.feature.discover.domain.usecase

import com.zc.vndb.feature.discover.domain.model.TrendingPage
import com.zc.vndb.feature.discover.domain.repository.DiscoverRepository
import org.koin.core.annotation.Factory

/**
 * One page of the full "New & Trending" list: the same rows as Discover's (see
 * [toTrendingReleases]), without visual novels already shown on earlier pages.
 */
@Factory
class GetTrendingReleasesPageUseCase(private val repository: DiscoverRepository) {
    suspend operator fun invoke(
        languages: List<String>,
        page: Int,
        shownNovelIds: Set<String>,
    ): Result<TrendingPage> {
        // No language chosen would be an empty "or" filter, which VNDB rejects.
        if (languages.isEmpty()) return Result.success(TrendingPage(emptyList(), hasMore = false))
        return repository.getRecentReleasesPage(languages, page, PAGE_SIZE).map { releases ->
            TrendingPage(
                rows = releases.releases
                    .toTrendingReleases(languages)
                    .filterNot { it.novelId in shownNovelIds },
                hasMore = releases.hasMore,
            )
        }
    }

    companion object {
        /** Releases per request; de-duplicating by visual novel leaves fewer rows. */
        const val PAGE_SIZE = 30
    }
}
