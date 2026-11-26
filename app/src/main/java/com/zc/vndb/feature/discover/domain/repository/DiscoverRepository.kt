package com.zc.vndb.feature.discover.domain.repository

import com.zc.vndb.feature.discover.domain.model.DatabaseStats
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import com.zc.vndb.feature.discover.domain.model.NovelDetails
import com.zc.vndb.feature.discover.domain.model.QuoteOfTheDay
import com.zc.vndb.feature.discover.domain.model.Release
import com.zc.vndb.feature.discover.domain.model.ReleasesPage

interface DiscoverRepository {

    /** Highest-rated novels with enough votes to be meaningful, best first. */
    suspend fun getTopRatedNovels(limit: Int): Result<List<DiscoverNovel>>

    suspend fun getNovelDetails(id: String): Result<NovelDetails>

    /** Releases already out in any of [languageCodes], newest first. */
    suspend fun getRecentReleases(languageCodes: List<String>, limit: Int): Result<List<Release>>

    /** Page [page] (from 1) of the same releases, [pageSize] at a time. */
    suspend fun getRecentReleasesPage(
        languageCodes: List<String>,
        page: Int,
        pageSize: Int,
    ): Result<ReleasesPage>

    suspend fun getDatabaseStats(): Result<DatabaseStats>

    /** The best-scored quotes from all-ages visual novels, best first. */
    suspend fun getTopQuotes(limit: Int): Result<List<QuoteOfTheDay>>

    /** Numeric part of the highest visual novel id (e.g. 70123 for "v70123"). */
    suspend fun getLatestNovelNumber(): Result<Int>

    /** Id of the first visual novel whose number is >= [number], or null if there is none. VNDB ids have gaps. */
    suspend fun getFirstNovelIdFrom(number: Int): Result<String?>
}
