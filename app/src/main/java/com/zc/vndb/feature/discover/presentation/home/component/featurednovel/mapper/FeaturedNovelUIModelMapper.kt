package com.zc.vndb.feature.discover.presentation.home.component.featurednovel.mapper

import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.ratingbadge.mapper.toRatingOutOfTenLabel
import com.zc.vndb.common.ui.mapper.bbCodeToPlainText
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.FeaturedNovel
import com.zc.vndb.feature.discover.presentation.home.component.featurednovel.model.FeaturedNovelUIModel
import kotlinx.collections.immutable.toImmutableList

fun FeaturedNovel.toUIModel(
    titleLanguage: TitleLanguage = TitleLanguage.Romanized,
): FeaturedNovelUIModel = FeaturedNovelUIModel(
    id = id,
    title = primaryTitle(title, originalTitle, titleLanguage),
    ratingLabel = rating?.toRatingOutOfTenLabel(),
    developerLabel = developerName?.let(UiText::StringValue),
    writerLabel = scenarioWriter?.let(UiText::StringValue),
    description = description?.bbCodeToPlainText()?.takeIf { it.isNotEmpty() }?.let(
        UiText::StringValue,
    ),
    coverUrl = cover?.url,
    isCoverSensitive = cover?.isSensitive ?: false,
    tags = tags.map(UiText::StringValue).toImmutableList(),
    ageLabel = if (isAllAges) UiText.StringResource(StringResource.discover_all_ages) else null,
)
