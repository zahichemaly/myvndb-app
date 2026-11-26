package com.zc.vndb.feature.detail.data.repository

import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.detail.data.mapper.charactersQuery
import com.zc.vndb.feature.detail.data.mapper.coversQuery
import com.zc.vndb.feature.detail.data.mapper.creditsQuery
import com.zc.vndb.feature.detail.data.mapper.editionsQuery
import com.zc.vndb.feature.detail.data.mapper.novelQuery
import com.zc.vndb.feature.detail.data.mapper.quotesQuery
import com.zc.vndb.feature.detail.data.mapper.releasesQuery
import com.zc.vndb.feature.detail.data.mapper.screenshotsQuery
import com.zc.vndb.feature.detail.data.mapper.toCovers
import com.zc.vndb.feature.detail.data.mapper.toDomain
import com.zc.vndb.feature.detail.data.remote.DetailService
import com.zc.vndb.feature.detail.domain.model.CoverArt
import com.zc.vndb.feature.detail.domain.model.Edition
import com.zc.vndb.feature.detail.domain.model.NovelCharacter
import com.zc.vndb.feature.detail.domain.model.NovelCredits
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.domain.model.Quote
import com.zc.vndb.feature.detail.domain.model.ReleaseSummary
import com.zc.vndb.feature.detail.domain.model.ResultPage
import com.zc.vndb.feature.detail.domain.model.Screenshot
import com.zc.vndb.feature.detail.domain.repository.DetailRepository
import org.koin.core.annotation.Single

@Single
class DetailRepositoryImpl(private val service: DetailService, appConfig: AppConfig) :
    DetailRepository {

    /** Images are judged by this build's rule: production never reveals sensitive ones. */
    private val policy = appConfig.sensitiveContentPolicy

    override suspend fun getNovel(id: String): Result<NovelDetail> = safeApiCall {
        val novel = service.queryNovel(
            novelQuery(id),
        ).results.firstOrNull() ?: throw DomainError.NotFound
        novel.toDomain(policy)
    }

    override suspend fun getCharacters(novelId: String): Result<List<NovelCharacter>> =
        safeApiCall {
            service.queryCharacters(
                charactersQuery(novelId),
            ).results.mapNotNull { it.toDomain(novelId, policy) }
        }

    override suspend fun getCredits(novelId: String): Result<NovelCredits> = safeApiCall {
        val credits = service.queryCredits(
            creditsQuery(novelId),
        ).results.firstOrNull() ?: throw DomainError.NotFound
        credits.toDomain()
    }

    override suspend fun getEditions(novelId: String): Result<List<Edition>> = safeApiCall {
        service.queryEditions(editionsQuery(novelId)).results.flatMap { it.toDomain() }
    }

    override suspend fun getScreenshots(novelId: String): Result<List<Screenshot>> = safeApiCall {
        service.queryScreenshots(screenshotsQuery(novelId)).results.firstOrNull()
            ?.screenshots.orEmpty()
            .mapNotNull { it.toDomain(policy) }
    }

    override suspend fun getCovers(novelId: String): Result<List<CoverArt>> = safeApiCall {
        service.queryCovers(coversQuery(novelId)).results.flatMap { it.toCovers(policy) }
    }

    override suspend fun getReleases(
        novelId: String,
        page: Int,
        pageSize: Int,
    ): Result<ResultPage<ReleaseSummary>> = safeApiCall {
        val result = service.queryReleases(releasesQuery(novelId, page, pageSize))
        ResultPage(items = result.results.map { it.toDomain() }, hasMore = result.more)
    }

    override suspend fun getQuotes(
        novelId: String,
        page: Int,
        pageSize: Int,
    ): Result<ResultPage<Quote>> = safeApiCall {
        val result = service.queryQuotes(quotesQuery(novelId, page, pageSize))
        ResultPage(items = result.results.map { it.toDomain() }, hasMore = result.more)
    }
}
