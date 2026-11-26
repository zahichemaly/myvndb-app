package com.zc.vndb.feature.release.data.mapper

import com.zc.vndb.common.data.mapper.coverOf
import com.zc.vndb.common.data.mapper.toCover
import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import com.zc.vndb.feature.release.data.dto.ReleaseDetailDTO
import com.zc.vndb.feature.release.data.dto.ReleaseImageDTO
import com.zc.vndb.feature.release.data.dto.ReleaseLanguageDTO
import com.zc.vndb.feature.release.data.dto.ReleaseNovelDTO
import com.zc.vndb.feature.release.data.dto.ReleaseProducerDTO
import com.zc.vndb.feature.release.domain.model.ReleaseDetail
import com.zc.vndb.feature.release.domain.model.ReleaseImage
import com.zc.vndb.feature.release.domain.model.ReleaseImageType
import com.zc.vndb.feature.release.domain.model.ReleaseLanguage
import com.zc.vndb.feature.release.domain.model.ReleaseMedium
import com.zc.vndb.feature.release.domain.model.ReleaseNovel
import com.zc.vndb.feature.release.domain.model.ReleaseProducer
import com.zc.vndb.feature.release.domain.model.ReleaseType
import com.zc.vndb.feature.release.domain.model.Resolution
import com.zc.vndb.feature.release.domain.model.Voicing

private const val RESOLUTION_PARTS = 2

fun ReleaseDetailDTO.toDomain(policy: SensitiveContentPolicy): ReleaseDetail = ReleaseDetail(
    id = id,
    title = title.orEmpty(),
    altTitle = alttitle?.takeIf { it.isNotBlank() && it != title },
    releaseDate = released.toReleaseDate(),
    minAge = minage,
    isOfficial = official ?: true,
    isPatch = patch ?: false,
    isFreeware = freeware ?: false,
    isUncensored = uncensored,
    hasAdultContent = hasEro ?: false,
    platforms = platforms.orEmpty(),
    media = media.orEmpty().map { ReleaseMedium(code = it.medium, quantity = it.qty ?: 0) },
    resolution = resolution.toResolution(),
    engine = engine?.takeIf { it.isNotBlank() },
    voicing = voiced?.let { Voicing.entries.getOrNull(it - 1) },
    catalog = catalog?.takeIf { it.isNotBlank() },
    gtin = gtin?.takeIf { it.isNotBlank() && it != "0" },
    notes = notes?.takeIf { it.isNotBlank() },
    languages = languages.orEmpty().map { it.toDomain() },
    novels = vns.orEmpty().map { it.toDomain(policy) },
    producers = producers.orEmpty().map { it.toDomain() },
    images = images.orEmpty().mapNotNull { it.toDomain(policy) },
    links = extlinks.orEmpty().map { ExternalLink(label = it.label, url = it.url) },
)

private fun List<Int>?.toResolution(): Resolution? =
    this?.takeIf { it.size == RESOLUTION_PARTS && it.all { size -> size > 0 } }
        ?.let { (width, height) -> Resolution(width = width, height = height) }

private fun ReleaseLanguageDTO.toDomain() = ReleaseLanguage(
    code = lang,
    title = title?.takeIf { it.isNotBlank() },
    isMain = main ?: false,
    isMachineTranslated = mtl ?: false,
)

private fun ReleaseProducerDTO.toDomain() = ReleaseProducer(
    id = id,
    name = name,
    isDeveloper = developer ?: false,
    isPublisher = publisher ?: false,
)

private fun ReleaseNovelDTO.toDomain(policy: SensitiveContentPolicy) = ReleaseNovel(
    id = id,
    title = title.orEmpty(),
    originalTitle = altTitle,
    releaseDate = released.toReleaseDate(),
    cover = image?.toCover(policy),
    type = when (rtype) {
        "partial" -> ReleaseType.Partial
        "trial" -> ReleaseType.Trial
        else -> ReleaseType.Complete
    },
)

/** Null for an image without a URL. */
private fun ReleaseImageDTO.toDomain(policy: SensitiveContentPolicy): ReleaseImage? {
    val imageUrl = url ?: return null
    return ReleaseImage(
        type = when (type) {
            "pkgfront" -> ReleaseImageType.PackageFront
            "pkgback" -> ReleaseImageType.PackageBack
            "pkgcontent" -> ReleaseImageType.PackageContents
            "pkgside" -> ReleaseImageType.PackageSide
            "pkgmed" -> ReleaseImageType.PackageMedia
            "dig" -> ReleaseImageType.Digital
            else -> ReleaseImageType.Other
        },
        image = coverOf(imageUrl, thumbnail, sexual, violence, policy),
    )
}
