package com.zc.vndb.feature.discover.presentation

import com.zc.vndb.common.ui.component.error.model.ErrorUIModel

sealed interface TrendingEffect {
    data object NavigateBack : TrendingEffect
    data class NavigateToNovel(val id: String) : TrendingEffect
    data class NavigateToRelease(val id: String) : TrendingEffect

    /** A refresh failed; the rows already shown stay. */
    data class ShowError(val error: ErrorUIModel) : TrendingEffect
}
