package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.repository.SessionRepository
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

/** The signed-in user's labels that can be put on an entry (every label but the virtual ones). */
@Factory
class GetEditableLabelsUseCase(
    private val sessions: SessionRepository,
    private val repository: UserListRepository,
) {

    suspend operator fun invoke(): Result<List<ListLabel>> {
        val session = sessions.session.first() ?: return Result.failure(DomainError.Unauthorized)
        return repository.getLabels(session.userId).map { labels ->
            labels.filterNot { it.id in MyListEntry.VIRTUAL_LABELS }.sortedBy { it.id }
        }
    }
}
