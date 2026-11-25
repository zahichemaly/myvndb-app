package com.zc.vndb.feature.profile.domain.repository

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.profile.domain.model.UserProfile

/** [calls] logs "user"; share it with `FakeUserListRepository` to see both in order. */
class FakeProfileRepository(val calls: MutableList<String> = mutableListOf()) : ProfileRepository {

    var user: UserProfile? = null

    override suspend fun getUser(userId: String): Result<UserProfile> {
        calls += "user"
        return user?.let { Result.success(it) } ?: Result.failure(DomainError.NotFound)
    }
}
