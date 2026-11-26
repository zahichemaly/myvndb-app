package com.zc.vndb.feature.release.data.repository

import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.release.data.mapper.releaseQuery
import com.zc.vndb.feature.release.data.mapper.toDomain
import com.zc.vndb.feature.release.data.remote.ReleaseService
import com.zc.vndb.feature.release.domain.model.ReleaseDetail
import com.zc.vndb.feature.release.domain.repository.ReleaseRepository
import org.koin.core.annotation.Single

@Single
class ReleaseRepositoryImpl(private val service: ReleaseService, appConfig: AppConfig) :
    ReleaseRepository {

    /** Images are judged by this build's rule: production never reveals sensitive ones. */
    private val policy = appConfig.sensitiveContentPolicy

    override suspend fun getRelease(id: String): Result<ReleaseDetail> = safeApiCall {
        val release = service.queryRelease(
            releaseQuery(id),
        ).results.firstOrNull() ?: throw DomainError.NotFound
        release.toDomain(policy)
    }
}
