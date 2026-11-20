package com.zc.vndb.feature.detail.presentation.component.taggroups.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TagGroupUIModel(val title: UiText, val tags: ImmutableList<TagChipUIModel>)
