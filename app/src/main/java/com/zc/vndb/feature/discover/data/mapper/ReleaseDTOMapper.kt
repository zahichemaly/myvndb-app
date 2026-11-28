package com.zc.vndb.feature.discover.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.discover.data.dto.ReleaseDTO
import com.zc.vndb.feature.discover.data.dto.ReleaseLanguageDTO
import com.zc.vndb.feature.discover.data.dto.ReleaseVisualNovelDTO
import com.zc.vndb.feature.discover.domain.model.Platform
import com.zc.vndb.feature.discover.domain.model.Release
import com.zc.vndb.feature.discover.domain.model.ReleaseLanguage
import com.zc.vndb.feature.discover.domain.model.ReleaseNovel

fun ReleaseDTO.toDomain(policy: SensitiveContentPolicy): Release = Release(
    id = id,
    releaseDate = released.toReleaseDate(),
    isPatch = patch ?: false,
    languages = languages.orEmpty().map { it.toDomain() },
    platforms = platforms.orEmpty().map(::Platform),
    novels = vns.orEmpty().map { it.toDomain(policy) },
)

fun ReleaseLanguageDTO.toDomain(): ReleaseLanguage =
    ReleaseLanguage(code = lang, isMachineTranslated = mtl ?: false)

fun ReleaseVisualNovelDTO.toDomain(policy: SensitiveContentPolicy): ReleaseNovel = ReleaseNovel(
    id = id,
    title = title.orEmpty(),
    originalTitle = altTitle,
    originalLanguage = olang.orEmpty(),
    releaseDate = released.toReleaseDate(),
    rating = rating,
    cover = image?.toCover(policy),
)
