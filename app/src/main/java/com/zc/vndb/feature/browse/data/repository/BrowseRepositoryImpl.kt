package com.zc.vndb.feature.browse.data.repository

import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.browse.data.mapper.producerQuery
import com.zc.vndb.feature.browse.data.mapper.staffQuery
import com.zc.vndb.feature.browse.data.mapper.tagQuery
import com.zc.vndb.feature.browse.data.mapper.toDomain
import com.zc.vndb.feature.browse.data.mapper.toQueryDTO
import com.zc.vndb.feature.browse.data.remote.BrowseService
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowsePage
import com.zc.vndb.feature.browse.domain.model.ProducerInfo
import com.zc.vndb.feature.browse.domain.model.StaffInfo
import com.zc.vndb.feature.browse.domain.model.TagInfo
import com.zc.vndb.feature.browse.domain.repository.BrowseRepository
import org.koin.core.annotation.Single

@Single
class BrowseRepositoryImpl(private val service: BrowseService, appConfig: AppConfig) :
    BrowseRepository {

    /** Images are judged by this build's rule: production never reveals sensitive ones. */
    private val policy = appConfig.sensitiveContentPolicy

    override suspend fun getTag(id: String): Result<TagInfo> = safeApiCall {
        val tag = service.queryTag(tagQuery(id)).results.firstOrNull() ?: throw DomainError.NotFound
        tag.toDomain()
    }

    override suspend fun getProducer(id: String): Result<ProducerInfo> = safeApiCall {
        val producer = service.queryProducer(producerQuery(id)).results.firstOrNull()
            ?: throw DomainError.NotFound
        producer.toDomain()
    }

    override suspend fun getStaff(id: String): Result<StaffInfo> = safeApiCall {
        val staff = service.queryStaff(staffQuery(id)).results.firstOrNull()
            ?: throw DomainError.NotFound
        staff.toDomain()
    }

    override suspend fun searchNovels(
        filter: BrowseFilter,
        page: Int,
        pageSize: Int,
        withCount: Boolean,
    ): Result<BrowsePage> = safeApiCall {
        service.queryNovels(filter.toQueryDTO(page, pageSize, withCount)).toDomain(policy)
    }
}
