package com.zc.vndb.feature.discover.domain.repository

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.discover.domain.model.DatabaseStats
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import com.zc.vndb.feature.discover.domain.model.NovelDetails
import com.zc.vndb.feature.discover.domain.model.QuoteOfTheDay
import com.zc.vndb.feature.discover.domain.model.Release
import com.zc.vndb.feature.discover.domain.model.ReleasesPage

/** In-memory [DiscoverRepository]. Set a `*Error` to make the matching call fail. */
class FakeDiscoverRepository : DiscoverRepository {

    var topRated: List<DiscoverNovel> = emptyList()
    val details = mutableMapOf<String, NovelDetails>()
    var releases: List<Release> = emptyList()

    /** Pages of [getRecentReleasesPage] by number; a missing page is empty with nothing after it. */
    val releasePages = mutableMapOf<Int, ReleasesPage>()
    val requestedReleasePages = mutableListOf<Int>()

    /** Fails [getRecentReleasesPage] for these page numbers. */
    val failingReleasePages = mutableSetOf<Int>()
    var stats = DatabaseStats(novelCount = 0)
    var latestNovelNumber = 0

    var quotes: List<QuoteOfTheDay> = emptyList()
    var quoteCalls = 0
    var requestedQuoteLimit: Int? = null

    /** Existing novel numbers, sorted; used by [getFirstNovelIdFrom]. */
    var novelNumbers: List<Int> = emptyList()

    var topRatedError: DomainError? = null
    var releasesError: DomainError? = null
    var statsError: DomainError? = null
    var latestError: DomainError? = null
    var quoteError: DomainError? = null

    var requestedTopRatedLimit: Int? = null
    var requestedReleaseLanguages: List<String>? = null
    var requestedReleaseLimit: Int? = null
    val requestedDetailIds = mutableListOf<String>()
    val requestedNovelNumbers = mutableListOf<Int>()

    override suspend fun getTopRatedNovels(limit: Int): Result<List<DiscoverNovel>> {
        requestedTopRatedLimit = limit
        return topRatedError?.let { Result.failure(it) } ?: Result.success(topRated.take(limit))
    }

    override suspend fun getNovelDetails(id: String): Result<NovelDetails> {
        requestedDetailIds += id
        return details[id]?.let { Result.success(it) } ?: Result.failure(DomainError.NotFound)
    }

    override suspend fun getRecentReleases(
        languageCodes: List<String>,
        limit: Int,
    ): Result<List<Release>> {
        requestedReleaseLanguages = languageCodes
        requestedReleaseLimit = limit
        return releasesError?.let { Result.failure(it) } ?: Result.success(releases.take(limit))
    }

    override suspend fun getRecentReleasesPage(
        languageCodes: List<String>,
        page: Int,
        pageSize: Int,
    ): Result<ReleasesPage> {
        requestedReleaseLanguages = languageCodes
        requestedReleaseLimit = pageSize
        requestedReleasePages += page
        if (page in failingReleasePages) return Result.failure(DomainError.Network)
        return Result.success(releasePages[page] ?: ReleasesPage(emptyList(), hasMore = false))
    }

    override suspend fun getDatabaseStats(): Result<DatabaseStats> =
        statsError?.let { Result.failure(it) } ?: Result.success(stats)

    override suspend fun getTopQuotes(limit: Int): Result<List<QuoteOfTheDay>> {
        quoteCalls++
        requestedQuoteLimit = limit
        return quoteError?.let { Result.failure(it) } ?: Result.success(quotes.take(limit))
    }

    override suspend fun getLatestNovelNumber(): Result<Int> =
        latestError?.let { Result.failure(it) } ?: Result.success(latestNovelNumber)

    override suspend fun getFirstNovelIdFrom(number: Int): Result<String?> {
        requestedNovelNumbers += number
        return Result.success(novelNumbers.firstOrNull { it >= number }?.let { "v$it" })
    }
}
