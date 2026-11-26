package com.zc.vndb.common.data.mapper

import com.zc.vndb.common.data.dto.ImageDTO
import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.SensitiveContentPolicy

/** An image without a URL is no cover. */
fun ImageDTO.toCover(policy: SensitiveContentPolicy): Cover? =
    url?.let { coverOf(it, thumbnail, sexual, violence, policy) }

/** Any VNDB image (cover, screenshot, release art) judged by [policy]; missing content flags count as safe (0). */
fun coverOf(
    url: String,
    thumbnail: String?,
    sexual: Double?,
    violence: Double?,
    policy: SensitiveContentPolicy,
): Cover = Cover(
    url = url,
    thumbnailUrl = thumbnail,
    sexual = sexual ?: 0.0,
    violence = violence ?: 0.0,
    policy = policy,
)
