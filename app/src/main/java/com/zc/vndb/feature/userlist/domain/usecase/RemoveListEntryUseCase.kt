package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import org.koin.core.annotation.Factory

/** Removes a VN, its vote, dates, notes and releases from the signed-in user's list. */
@Factory
class RemoveListEntryUseCase(private val repository: UserListRepository) {

    suspend operator fun invoke(novelId: String): Result<Unit> = repository.removeEntry(novelId)
}
