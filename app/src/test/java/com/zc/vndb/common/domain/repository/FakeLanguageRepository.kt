package com.zc.vndb.common.domain.repository

import com.zc.vndb.common.domain.model.BuiltInLanguages
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.LanguageOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [LanguageRepository]: set [current] to change the list, [refreshError] to fail a refresh. */
class FakeLanguageRepository(initial: List<LanguageOption> = BuiltInLanguages) :
    LanguageRepository {

    val current = MutableStateFlow(initial)
    override val languages: Flow<List<LanguageOption>> = current

    var refreshError: DomainError? = null
    var refreshCalls = 0

    override suspend fun refreshIfStale(): Result<Unit> {
        refreshCalls++
        return refreshError?.let { Result.failure(it) } ?: Result.success(Unit)
    }
}
