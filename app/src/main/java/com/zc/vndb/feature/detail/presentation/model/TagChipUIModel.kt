package com.zc.vndb.feature.detail.presentation.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText

/** A tappable tag: opens Browse scoped to [id]. */
@Immutable
data class TagChipUIModel(val id: String, val label: UiText)
