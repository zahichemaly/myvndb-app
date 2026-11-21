package com.zc.vndb.feature.userlist.presentation.model

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.text.UiText

/** A list label the entry can have: a reading status or another label (Wishlist, own labels). */
@Immutable
data class LabelOptionUIModel(val id: Int, val label: UiText, val isSelected: Boolean)
