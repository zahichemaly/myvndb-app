package com.zc.vndb.feature.discover.presentation.home.component.novelcard.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText

@Immutable
data class NovelCardUIModel(
    val id: String,
    val title: UiText,
    val subtitle: UiText?,
    val ratingLabel: UiText?,
    val coverUrl: String?,
    val isCoverSensitive: Boolean,
)
