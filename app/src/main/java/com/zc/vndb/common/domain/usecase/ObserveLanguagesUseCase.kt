package com.zc.vndb.common.domain.usecase

import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

/** The languages to offer in a picker; never empty, and it never touches the network. */
@Factory
class ObserveLanguagesUseCase(private val repository: LanguageRepository) {
    operator fun invoke(): Flow<List<LanguageOption>> = repository.languages
}
