package com.zc.vndb.feature.profile.data.mapper

import com.zc.vndb.feature.profile.data.dto.UserDTO
import com.zc.vndb.feature.profile.domain.model.UserProfile

fun UserDTO.toDomain() = UserProfile(
    id = id,
    username = username,
    lengthVotes = lengthVotes ?: 0,
    lengthVotesMinutes = lengthVotesSum ?: 0,
)
