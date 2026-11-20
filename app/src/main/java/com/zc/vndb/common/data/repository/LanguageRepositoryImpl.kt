package com.zc.vndb.common.data.repository

import com.zc.vndb.common.data.local.LanguageLocalDataSource
import com.zc.vndb.common.data.remote.SchemaService
import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.BuiltInLanguages
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.repository.LanguageRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class LanguageRepositoryImpl(
    private val local: LanguageLocalDataSource,
    private val service: SchemaService,
    private val clock: Clock,
) : LanguageRepository {

    override val languages: Flow<List<LanguageOption>> =
        local.cached.map { cached -> cached?.languages ?: BuiltInLanguages }

    override suspend fun refreshIfStale(): Result<Unit> = safeApiCall {
        val cached = local.cached.first()
        val now = clock.now()
        if (cached == null || now - cached.fetchedAt >= STALE_AFTER) {
            val fetched = service.getSchema().enums.language
                .map { LanguageOption(code = it.id, label = it.label) }
            // An empty list would replace a working cache with nothing.
            if (fetched.isEmpty()) throw DomainError.InvalidData
            local.save(fetched, now)
        }
    }

    private companion object {
        /** VNDB's language list changes about once a year. */
        val STALE_AFTER = 30.days
    }
}
