package com.zc.vndb.feature.profile.domain.repository

import com.zc.vndb.feature.profile.domain.model.UserProfile

/** Public VNDB user data. The user's list lives in the userlist feature's `UserListRepository`. */
interface ProfileRepository {

    suspend fun getUser(userId: String): Result<UserProfile>
}
