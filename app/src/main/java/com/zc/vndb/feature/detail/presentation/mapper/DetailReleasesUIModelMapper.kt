package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.ui.mapper.languageFlag
import com.zc.vndb.common.ui.mapper.platformName
import com.zc.vndb.common.ui.mapper.toIsoLabel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.ReleaseSummary
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toMorePlatformsLabel
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toPlatformIcons
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.EditionRowUIModel
import com.zc.vndb.feature.detail.presentation.model.ReleaseGroupUIModel
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Releases grouped by language as on vndb.org: a release in several languages appears in each,
 * biggest groups first. Rows reuse the edition row (platforms instead of languages).
 */
fun List<ReleaseSummary>.toReleaseGroups(): ImmutableList<ReleaseGroupUIModel> =
    flatMap { release -> release.languageCodes.ifEmpty { listOf("") }.map { it to release } }
        .groupBy({ it.first }, { it.second })
        .entries
        .sortedByDescending { it.value.size }
        .map { (code, releases) ->
            ReleaseGroupUIModel(
                languageCode = code,
                title = code.toGroupTitle(releases.size),
                releases = releases.map { it.toRowUIModel(code) }.toImmutableList(),
            )
        }
        .toImmutableList()

/** "🇯🇵 Japanese (12)", without the flag when the language has none. */
private fun String.toGroupTitle(count: Int): UiText = UiText.StringValue(
    listOfNotNull(languageFlag(this), "${toLanguageName()} ($count)").joinToString(" "),
)

private fun ReleaseSummary.toRowUIModel(languageCode: String) = EditionRowUIModel(
    key = "$languageCode-$id",
    releaseId = id,
    title = UiText.StringValue(title),
    platformLabel = UiText.StringValue(platforms.joinToString(", ") { platformName(it) }),
    languagesLabel = null,
    dateLabel = releaseDate.toIsoLabel(),
    family = family,
    platformIcons = platforms.toPlatformIcons(),
    morePlatformsLabel = platforms.toMorePlatformsLabel(),
)

private fun String.toLanguageName(): String =
    if (isEmpty()) "?" else Locale.forLanguageTag(this).displayLanguage.ifBlank { this }
