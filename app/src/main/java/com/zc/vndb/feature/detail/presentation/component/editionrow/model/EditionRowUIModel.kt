package com.zc.vndb.feature.detail.presentation.component.editionrow.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.domain.model.PlatformFamily
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.text.UiText
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class EditionRowUIModel(
    val key: String,
    val releaseId: String,
    val title: UiText,
    val platformLabel: UiText,
    val languagesLabel: UiText?,
    val dateLabel: UiText,
    val family: PlatformFamily,
    /** One icon per distinct platform icon, capped; [morePlatformsLabel] counts the rest ("+2"). */
    val platformIcons: ImmutableList<PlatformIcon>,
    val morePlatformsLabel: UiText?,
)
