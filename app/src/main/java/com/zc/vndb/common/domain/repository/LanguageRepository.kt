package com.zc.vndb.common.domain.repository

import com.zc.vndb.common.domain.model.LanguageOption
import kotlinx.coroutines.flow.Flow

/** The languages VNDB supports, for every picker that offers a choice of language. */
interface LanguageRepository {

    /** The cached list, or the built-in common languages until one has been fetched. */
    val languages: Flow<List<LanguageOption>>

    /**
     * Fetches VNDB's list when nothing is cached or the cache is older than 30 days, and caches it.
     * A failure leaves the cache (or the built-in list) as it was.
     */
    suspend fun refreshIfStale(): Result<Unit>
}
