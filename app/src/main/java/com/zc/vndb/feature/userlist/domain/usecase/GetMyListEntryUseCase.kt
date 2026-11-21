package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.repository.SessionRepository
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

/** The signed-in user's entry for a VN; null when it is not on their list or nobody is signed in. */
@Factory
class GetMyListEntryUseCase(
    private val sessions: SessionRepository,
    private val repository: UserListRepository,
) {

    suspend operator fun invoke(novelId: String): Result<MyListEntry?> {
        val session = sessions.session.first() ?: return Result.success(null)
        return repository.getMyEntry(session.userId, novelId)
    }
}
