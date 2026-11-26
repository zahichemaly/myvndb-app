package com.zc.vndb.feature.discover.domain.usecase

import com.zc.vndb.feature.discover.domain.model.TrendingRelease
import com.zc.vndb.feature.discover.domain.repository.DiscoverRepository
import org.koin.core.annotation.Factory

/**
 * "New & Trending": the newest releases in the user's [languages], one row per visual novel,
 * each labelled with why it is new. Machine-translation-only releases are left out.
 */
@Factory
class GetTrendingReleasesUseCase(private val repository: DiscoverRepository) {
    suspend operator fun invoke(languages: List<String>): Result<List<TrendingRelease>> =
        repository.getRecentReleases(languages, FETCH_LIMIT).map { releases ->
            releases.toTrendingReleases(languages).take(RESULT_LIMIT)
        }

    companion object {
        const val RESULT_LIMIT = 10

        // Over-fetch so de-duplicating by visual novel still leaves RESULT_LIMIT rows.
        const val FETCH_LIMIT = 30
    }
}
