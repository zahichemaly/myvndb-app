package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import org.koin.core.annotation.Factory

/** Every label on [userId]'s list, with its entry count; private ones only for the signed-in user. */
@Factory
class GetListLabelsUseCase(private val repository: UserListRepository) {

    suspend operator fun invoke(userId: String): Result<List<ListLabel>> =
        repository.getLabels(userId)
}
