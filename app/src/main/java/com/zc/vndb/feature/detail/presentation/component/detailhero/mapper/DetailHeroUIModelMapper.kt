package com.zc.vndb.feature.detail.presentation.component.detailhero.mapper

import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.ratingbadge.mapper.toRatingLabel
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.mapper.secondaryTitle
import com.zc.vndb.common.ui.mapper.toIsoLabel
import com.zc.vndb.common.ui.mapper.toLabel
import com.zc.vndb.common.ui.mapper.toNameLabel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.AgeRange
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.DetailHeroUIModel
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.HeroDeveloperUIModel
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlin.math.roundToInt
import kotlinx.collections.immutable.toImmutableList

private const val HERO_TAGS = 2
private const val MAX_DEVELOPERS = 2
private const val MINUTES_PER_HOUR = 60.0
private const val ALL_AGES = 0

fun NovelDetail.toHeroUIModel(
    ageRange: AgeRange?,
    titleLanguage: TitleLanguage = TitleLanguage.Romanized,
): DetailHeroUIModel = toHeroUIModel(
    ageRange,
    primaryTitle(title, originalTitle, titleLanguage),
    secondaryTitle(title, originalTitle, titleLanguage),
)

private fun NovelDetail.toHeroUIModel(
    ageRange: AgeRange?,
    primaryTitle: UiText,
    secondaryTitle: UiText?,
) = DetailHeroUIModel(
    title = primaryTitle,
    originalTitle = secondaryTitle,
    coverUrl = cover?.url,
    isCoverSensitive = cover?.isSensitive ?: false,
    developers = developers.take(MAX_DEVELOPERS)
        .map { HeroDeveloperUIModel(id = it.id, name = it.name) }
        .toImmutableList(),
    releasedLabel = releaseDate.toIsoLabel(),
    lengthName = length?.toNameLabel(),
    lengthValueLabel = (averageHoursLabel() ?: length?.toLabel())
        ?.let { UiText.StringResource(StringResource.detail_length_value, listOf(it)) },
    lengthVotesLabel = lengthVotes.takeIf { it > 0 && averagePlayMinutes != null }
        ?.let {
            UiText.StringResource(
                StringResource.detail_length_votes,
                listOf("%,d".format(it)),
            )
        },
    scoreLabel = rating?.let {
        UiText.StringResource(StringResource.detail_score_value, listOf(it.toRatingLabel()))
    },
    scoreVotesLabel = rating?.let {
        UiText.StringPlural(PluralResource.detail_votes, voteCount, listOf("%,d".format(voteCount)))
    },
    ageLabel = ageRange?.toLabel(),
    tags = tags
        .filter { it.category == TagCategory.Content && it.spoilerLevel == 0 }
        .sortedByDescending { it.rating }
        .take(HERO_TAGS)
        .map { TagChipUIModel(id = it.id, label = UiText.StringValue(it.name)) }
        .toImmutableList(),
)

private fun NovelDetail.averageHoursLabel(): UiText? = averagePlayMinutes?.let { minutes ->
    UiText.StringResource(
        StringResource.detail_hours,
        listOf((minutes / MINUTES_PER_HOUR).roundToInt().toString()),
    )
}

/** "All Ages", "18+", "All Ages / 15+" or "12+ / 18+". */
fun AgeRange.toLabel(): UiText = when {
    max == ALL_AGES -> UiText.StringResource(StringResource.detail_age_all)
    min == max -> UiText.StringResource(StringResource.detail_age_min, listOf(max))
    min == ALL_AGES -> UiText.StringResource(StringResource.detail_age_range_from_all, listOf(max))
    else -> UiText.StringResource(StringResource.detail_age_range, listOf(min, max))
}
