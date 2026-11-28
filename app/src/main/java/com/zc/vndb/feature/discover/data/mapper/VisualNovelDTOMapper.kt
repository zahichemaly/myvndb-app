package com.zc.vndb.feature.discover.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toDomain
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.discover.data.dto.VisualNovelDTO
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import com.zc.vndb.feature.discover.domain.model.NovelDetails

fun VisualNovelDTO.toDiscoverNovel(policy: SensitiveContentPolicy): DiscoverNovel = DiscoverNovel(
    id = id,
    title = title.orEmpty(),
    originalTitle = altTitle,
    rating = rating,
    cover = image?.toCover(policy),
    developerName = developers?.firstOrNull()?.name,
)

fun VisualNovelDTO.toNovelDetails(policy: SensitiveContentPolicy): NovelDetails = NovelDetails(
    id = id,
    title = title.orEmpty(),
    originalTitle = altTitle,
    rating = rating,
    cover = image?.toCover(policy),
    description = description?.takeIf { it.isNotBlank() },
    developerNames = developers.orEmpty().map { it.name },
    staff = staff.orEmpty().map { it.toDomain() },
    tags = tags.orEmpty().mapNotNull { it.toDomain() },
)

/** The numeric part of a VN id ("v2002" → 2002). */
fun VisualNovelDTO.idNumber(): Int = id.removePrefix(
    "v",
).toIntOrNull() ?: throw DomainError.InvalidData
