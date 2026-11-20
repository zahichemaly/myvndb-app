package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.data.mapper.toReleaseDate
import com.zc.vndb.common.domain.model.Cover
import com.zc.vndb.common.domain.model.VoiceActor
import com.zc.vndb.feature.detail.data.dto.CoverReleaseDTO
import com.zc.vndb.feature.detail.data.dto.NovelCreditsDTO
import com.zc.vndb.feature.detail.data.dto.QuoteDTO
import com.zc.vndb.feature.detail.data.dto.ReleaseSummaryDTO
import com.zc.vndb.feature.detail.data.dto.ScreenshotDTO
import com.zc.vndb.feature.detail.data.dto.StaffCreditDTO
import com.zc.vndb.feature.detail.domain.model.CoverArt
import com.zc.vndb.feature.detail.domain.model.NovelCredits
import com.zc.vndb.feature.detail.domain.model.PlatformFamily
import com.zc.vndb.feature.detail.domain.model.Quote
import com.zc.vndb.feature.detail.domain.model.ReleaseSummary
import com.zc.vndb.feature.detail.domain.model.Screenshot
import com.zc.vndb.feature.detail.domain.model.StaffCredit
import com.zc.vndb.feature.detail.domain.model.StaffCreditRole
import com.zc.vndb.feature.detail.domain.model.StaffEdition

/* DTO → domain for the lazily loaded sections of the detail page. */

private val coverImageTypes = setOf("pkgfront", "dig")

fun NovelCreditsDTO.toDomain(): NovelCredits = NovelCredits(
    staff = staff.orEmpty().map { it.toDomain() },
    editions = editions.orEmpty().map {
        StaffEdition(
            id = it.eid,
            name = it.name,
            languageCode = it.lang,
            isOfficial = it.official ?: false,
        )
    },
    voiceActors = va.orEmpty()
        .groupBy { it.character.id }
        .mapValues { (_, actors) ->
            actors.first().staff.let { VoiceActor(id = it.id, name = it.name) }
        },
)

private fun StaffCreditDTO.toDomain() = StaffCredit(
    staffId = id,
    name = name,
    role = when (role) {
        "scenario" -> StaffCreditRole.Scenario
        "chardesign" -> StaffCreditRole.CharacterDesign
        "art" -> StaffCreditRole.Art
        "music" -> StaffCreditRole.Music
        "songs" -> StaffCreditRole.Songs
        "director" -> StaffCreditRole.Director
        "translator" -> StaffCreditRole.Translator
        "editor" -> StaffCreditRole.Editor
        "qa" -> StaffCreditRole.QualityAssurance
        "staff" -> StaffCreditRole.Staff
        else -> StaffCreditRole.Other
    },
    note = note?.takeIf { it.isNotBlank() },
    editionId = eid,
)

/** Null for a screenshot without a URL. */
fun ScreenshotDTO.toDomain(): Screenshot? {
    val imageUrl = url ?: return null
    return Screenshot(
        image = Cover(
            url = imageUrl,
            thumbnailUrl = thumbnail,
            sexual = sexual ?: 0.0,
            violence = violence ?: 0.0,
        ),
        releaseId = release?.id,
        releaseTitle = release?.title,
    )
}

/** Package fronts and digital covers of the release; other package art is left out. */
fun CoverReleaseDTO.toCovers(): List<CoverArt> = images.orEmpty()
    .filter { it.type in coverImageTypes && it.url != null }
    .map {
        CoverArt(
            image = Cover(
                url = checkNotNull(it.url),
                thumbnailUrl = it.thumbnail,
                sexual = it.sexual ?: 0.0,
                violence = it.violence ?: 0.0,
            ),
            releaseId = id,
            releaseTitle = title.orEmpty(),
            isPackageFront = it.type == "pkgfront",
        )
    }

fun ReleaseSummaryDTO.toDomain(): ReleaseSummary = ReleaseSummary(
    id = id,
    title = title.orEmpty(),
    releaseDate = released.toReleaseDate(),
    platforms = platforms.orEmpty(),
    family = platforms?.firstOrNull()?.let { PlatformFamily.fromCode(it) } ?: PlatformFamily.Other,
    languageCodes = languages.orEmpty().map { it.lang },
    isOfficial = official ?: true,
    isPatch = patch ?: false,
    isFreeware = freeware ?: false,
    minAge = minage,
)

fun QuoteDTO.toDomain(): Quote = Quote(
    id = id,
    text = quote,
    score = score ?: 0,
    characterId = character?.id,
    characterName = character?.name,
)
