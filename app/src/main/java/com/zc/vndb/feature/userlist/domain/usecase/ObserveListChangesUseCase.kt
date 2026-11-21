package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

/** Emits a VN id each time its list entry is saved or removed in the app. */
@Factory
class ObserveListChangesUseCase(private val repository: UserListRepository) {

    operator fun invoke(): Flow<String> = repository.listChanges
}
