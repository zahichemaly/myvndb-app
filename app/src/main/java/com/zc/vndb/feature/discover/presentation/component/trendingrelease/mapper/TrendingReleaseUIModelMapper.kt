package com.zc.vndb.feature.discover.presentation.component.trendingrelease.mapper

import com.zc.vndb.common.domain.model.ReleaseDate
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.platformicon.mapper.toMorePlatformsLabel
import com.zc.vndb.common.ui.component.platformicon.mapper.toPlatformIcons
import com.zc.vndb.common.ui.component.ratingbadge.mapper.toRatingLabel
import com.zc.vndb.common.ui.mapper.languageName
import com.zc.vndb.common.ui.mapper.platformName
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.ReleaseKind
import com.zc.vndb.feature.discover.domain.model.TrendingRelease
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.ReleaseKindTone
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

private val dayFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day(Padding.NONE)
}

private val monthYearFormat = YearMonth.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
}

fun TrendingRelease.toUIModel(
    titleLanguage: TitleLanguage = TitleLanguage.Romanized,
): TrendingReleaseUIModel = TrendingReleaseUIModel(
    releaseId = releaseId,
    novelId = novelId,
    title = primaryTitle(title, originalTitle, titleLanguage),
    kindLabel = kind.toLabel(),
    kindTone = kind.toTone(),
    dateLabel = releaseDate.toLabel(),
    ratingLabel = rating?.toRatingLabel(),
    thumbnailUrl = cover?.thumbnailUrl ?: cover?.url,
    isCoverSensitive = cover?.isSensitive ?: false,
    languageLabel = translationLanguage?.let { UiText.StringValue(languageName(it)) },
    platformIcons = platforms.map { it.code }.toPlatformIcons(),
    morePlatformsLabel = platforms.map { it.code }.toMorePlatformsLabel(),
    platformsLabel = platforms.takeIf { it.isNotEmpty() }?.let { list ->
        UiText.StringValue(list.joinToString(", ") { platformName(it.code) })
    },
    opensRelease = kind == ReleaseKind.NewTranslation || kind == ReleaseKind.RecentPatch,
)

/** "Nov 18" for a full date (trending releases are recent), "Oct 2024", "2024" or "TBA" otherwise. */
fun ReleaseDate.toLabel(): UiText = when (this) {
    is ReleaseDate.Full -> UiText.StringValue(dayFormat.format(date))
    is ReleaseDate.Month -> UiText.StringValue(monthYearFormat.format(yearMonth))
    is ReleaseDate.Year -> UiText.StringValue(year.toString())
    ReleaseDate.Tba -> UiText.StringResource(StringResource.release_tba)
}

private fun ReleaseKind.toLabel(): UiText = UiText.StringResource(
    when (this) {
        ReleaseKind.NewRelease -> StringResource.discover_release_new
        ReleaseKind.NewTranslation -> StringResource.discover_release_translation
        ReleaseKind.RecentPatch -> StringResource.discover_release_patch
        ReleaseKind.ReRelease -> StringResource.discover_release_rerelease
    },
)

private fun ReleaseKind.toTone(): ReleaseKindTone = when (this) {
    ReleaseKind.NewRelease -> ReleaseKindTone.New
    ReleaseKind.NewTranslation -> ReleaseKindTone.Translation
    ReleaseKind.RecentPatch -> ReleaseKindTone.Patch
    ReleaseKind.ReRelease -> ReleaseKindTone.Rerelease
}
