package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toDomain
import com.zc.vndb.common.data.mapper.toNovelLength
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.feature.detail.data.dto.DetailNovelDTO
import com.zc.vndb.feature.detail.data.dto.RelationDTO
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.domain.model.NovelDeveloper
import com.zc.vndb.feature.detail.domain.model.NovelTitle
import com.zc.vndb.feature.detail.domain.model.RelatedNovel
import com.zc.vndb.feature.detail.domain.model.RelationType

fun DetailNovelDTO.toDomain(): NovelDetail = NovelDetail(
    id = id,
    title = title.orEmpty(),
    originalTitle = alttitle?.takeIf { it.isNotBlank() && it != title },
    releaseDate = released.toReleaseDate(),
    rating = rating,
    average = average,
    voteCount = votecount ?: 0,
    cover = image?.toCover(),
    developers = developers.orEmpty().map { NovelDeveloper(id = it.id, name = it.name) },
    description = description?.takeIf { it.isNotBlank() },
    length = toNovelLength(lengthMinutes = lengthMinutes, estimate = length),
    averagePlayMinutes = lengthMinutes,
    tags = tags.orEmpty().mapNotNull { it.toDomain() },
    links = extlinks.orEmpty().map { ExternalLink(label = it.label, url = it.url) },
    relations = relations.orEmpty().map { it.toDomain() },
    titles = titles.orEmpty().map {
        NovelTitle(
            languageCode = it.lang,
            title = it.title,
            latin = it.latin?.takeIf { latin -> latin.isNotBlank() && latin != it.title },
            isOfficial = it.official ?: false,
            isMain = it.main ?: false,
        )
    },
    aliases = aliases.orEmpty().filter { it.isNotBlank() }.distinct(),
    lengthVotes = lengthVotes ?: 0,
    originalLanguage = olang?.takeIf { it.isNotBlank() },
    languages = languages.orEmpty(),
)

fun RelationDTO.toDomain(): RelatedNovel = RelatedNovel(
    id = id,
    title = title.orEmpty(),
    originalTitle = altTitle,
    relation = relation.toRelationType(),
    isOfficial = relationOfficial ?: false,
    releaseDate = released.toReleaseDate(),
    rating = rating,
)

private fun String?.toRelationType(): RelationType = when (this) {
    "seq" -> RelationType.Sequel
    "preq" -> RelationType.Prequel
    "set" -> RelationType.SameSetting
    "alt" -> RelationType.AlternativeVersion
    "char" -> RelationType.SharesCharacters
    "side" -> RelationType.SideStory
    "par" -> RelationType.ParentStory
    "ser" -> RelationType.SameSeries
    "fan" -> RelationType.FanDisc
    "orig" -> RelationType.OriginalGame
    else -> RelationType.Other
}
