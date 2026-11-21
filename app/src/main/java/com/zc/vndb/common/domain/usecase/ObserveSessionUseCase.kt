package com.zc.vndb.common.domain.usecase

import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Factory

@Factory
class ObserveSessionUseCase(private val repository: SessionRepository) {

    operator fun invoke(): Flow<Session?> = repository.session.distinctUntilChanged()
}
