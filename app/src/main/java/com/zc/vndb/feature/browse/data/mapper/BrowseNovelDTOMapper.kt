package com.zc.vndb.feature.browse.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toDomain
import com.zc.vndb.common.data.mapper.toNovelLength
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.browse.data.dto.BrowseNovelDTO
import com.zc.vndb.feature.browse.data.dto.BrowseNovelPageDTO
import com.zc.vndb.feature.browse.domain.model.BrowseNovel
import com.zc.vndb.feature.browse.domain.model.BrowsePage

fun BrowseNovelPageDTO.toDomain(policy: SensitiveContentPolicy): BrowsePage = BrowsePage(
    novels = results.map { it.toDomain(policy) },
    totalCount = count,
    hasMore = more,
)

fun BrowseNovelDTO.toDomain(policy: SensitiveContentPolicy): BrowseNovel = BrowseNovel(
    id = id,
    title = title.orEmpty(),
    originalTitle = alttitle?.takeIf { it.isNotBlank() && it != title },
    releaseDate = released.toReleaseDate(),
    rating = rating,
    cover = image?.toCover(policy),
    developerNames = developers.orEmpty().map { it.name },
    tags = tags.orEmpty().mapNotNull { it.toDomain() },
    length = toNovelLength(lengthMinutes = lengthMinutes, estimate = length),
    languages = languages.orEmpty(),
)
