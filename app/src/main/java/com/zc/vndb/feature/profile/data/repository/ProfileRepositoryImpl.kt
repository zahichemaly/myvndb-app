package com.zc.vndb.feature.profile.data.repository

import com.zc.vndb.common.data.util.safeApiCall
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.profile.data.mapper.toDomain
import com.zc.vndb.feature.profile.data.remote.ProfileService
import com.zc.vndb.feature.profile.domain.model.UserProfile
import com.zc.vndb.feature.profile.domain.repository.ProfileRepository
import org.koin.core.annotation.Single

@Single
class ProfileRepositoryImpl(private val service: ProfileService) : ProfileRepository {

    override suspend fun getUser(userId: String): Result<UserProfile> = safeApiCall {
        service.user(userId)[userId]?.toDomain() ?: throw DomainError.NotFound
    }
}
