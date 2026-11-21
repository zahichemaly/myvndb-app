package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.DetailTabUIModel

/** A tab's label, with its item count when that is known ([count] null: not loaded, or more to load). */
fun DetailTab.toUIModel(count: Int?): DetailTabUIModel {
    val name = toLabel()
    return DetailTabUIModel(
        tab = this,
        label = count?.let {
            UiText.StringResource(StringResource.detail_tab_with_count, listOf(name, it))
        } ?: name,
    )
}

private fun DetailTab.toLabel(): UiText = UiText.StringResource(
    when (this) {
        DetailTab.Overview -> StringResource.detail_tab_overview
        DetailTab.Media -> StringResource.detail_tab_media
        DetailTab.Cast -> StringResource.detail_tab_cast
        DetailTab.Releases -> StringResource.detail_tab_releases
        DetailTab.Quotes -> StringResource.detail_tab_quotes
    },
)
