package com.zc.vndb.common.domain.usecase

import com.zc.vndb.common.domain.repository.LanguageRepository
import org.koin.core.annotation.Factory

/**
 * Asks for VNDB's language list when a picker is about to be used. It does nothing while the
 * cache is fresh, and callers can ignore a failure: the picker keeps what it has.
 */
@Factory
class RefreshLanguagesUseCase(private val repository: LanguageRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshIfStale()
}
