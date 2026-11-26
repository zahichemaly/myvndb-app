package com.zc.vndb.feature.discover.presentation.home.component.featurednovel.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FeaturedNovelUIModel(
    val id: String,
    val title: UiText,
    val ratingLabel: UiText?,
    val developerLabel: UiText?,
    val writerLabel: UiText?,
    val description: UiText?,
    val coverUrl: String?,
    val isCoverSensitive: Boolean,
    val tags: ImmutableList<UiText> = persistentListOf(),
    val ageLabel: UiText? = null,
)
