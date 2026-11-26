package com.zc.vndb.feature.discover.presentation.home.component.novelcard.mapper

import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.ui.component.ratingbadge.mapper.toRatingLabel
import com.zc.vndb.common.ui.mapper.primaryTitle
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import com.zc.vndb.feature.discover.presentation.home.component.novelcard.model.NovelCardUIModel

/** Cards are small, so the thumbnail is preferred over the full cover. */
fun DiscoverNovel.toNovelCardUIModel(
    titleLanguage: TitleLanguage = TitleLanguage.Romanized,
): NovelCardUIModel = NovelCardUIModel(
    id = id,
    title = primaryTitle(title, originalTitle, titleLanguage),
    subtitle = developerName?.let(UiText::StringValue),
    ratingLabel = rating?.toRatingLabel(),
    coverUrl = cover?.thumbnailUrl ?: cover?.url,
    isCoverSensitive = cover?.isSensitive ?: false,
)
