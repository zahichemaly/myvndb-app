package com.zc.vndb.feature.discover.presentation.home.mapper

import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.domain.model.DatabaseStats

/** "68,053 Visual Novels", grouped for the device locale. */
fun DatabaseStats.toCountLabel(): UiText = UiText.StringPlural(
    PluralResource.discover_stats_novels,
    novelCount,
    listOf("%,d".format(novelCount)),
)
