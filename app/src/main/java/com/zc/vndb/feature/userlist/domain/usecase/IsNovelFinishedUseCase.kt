package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.repository.SessionRepository
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

/** Whether the signed-in user has [novelId] on their Finished list; false for guests or on failure. */
@Factory
class IsNovelFinishedUseCase(
    private val sessions: SessionRepository,
    private val repository: UserListRepository,
) {

    suspend operator fun invoke(novelId: String): Boolean {
        val session = sessions.session.first() ?: return false
        return repository.getEntryLabelIds(session.userId, novelId)
            .getOrNull()
            .orEmpty()
            .contains(ListLabel.FINISHED)
    }
}
