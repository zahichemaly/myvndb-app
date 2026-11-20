package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.feature.detail.data.dto.EditionDTO
import com.zc.vndb.feature.detail.domain.model.Edition
import com.zc.vndb.feature.detail.domain.model.PlatformFamily

/** One edition per platform of the release. */
fun EditionDTO.toDomain(): List<Edition> = platforms.orEmpty().ifEmpty {
    listOf(
        "",
    )
}.map { platform ->
    Edition(
        id = id,
        title = title.orEmpty(),
        releaseDate = released.toReleaseDate(),
        platform = platform,
        family = PlatformFamily.fromCode(platform),
        languageCodes = languages.orEmpty().map { it.lang },
        isOfficial = official ?: false,
        isPatch = patch ?: false,
        minAge = minage,
    )
}
