package com.zc.vndb.feature.discover.presentation.home

import com.zc.vndb.common.ui.component.error.model.ErrorUIModel

sealed interface DiscoverEffect {
    data class NavigateToNovel(val id: String) : DiscoverEffect
    data class NavigateToRelease(val id: String) : DiscoverEffect
    data object NavigateToBrowse : DiscoverEffect
    data object NavigateToTopRated : DiscoverEffect
    data object NavigateToTrending : DiscoverEffect
    data class ShowError(val error: ErrorUIModel) : DiscoverEffect
}
