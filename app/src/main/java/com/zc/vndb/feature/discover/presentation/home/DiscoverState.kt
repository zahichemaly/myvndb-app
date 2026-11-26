package com.zc.vndb.feature.discover.presentation.home

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.model.TrendingReleaseUIModel
import com.zc.vndb.feature.discover.presentation.home.component.featurednovel.model.FeaturedNovelUIModel
import com.zc.vndb.feature.discover.presentation.home.component.novelcard.model.NovelCardUIModel
import com.zc.vndb.feature.discover.presentation.home.component.quoteoftheday.model.QuoteOfTheDayUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DiscoverState(
    val isLoading: Boolean = false,
    /** Pull to refresh in flight; the loaded sections stay on screen. */
    val isRefreshing: Boolean = false,
    /** Set only when the screen has nothing to show; section failures just hide that section. */
    val error: ErrorUIModel? = null,
    val featured: FeaturedNovelUIModel? = null,
    val topRated: ImmutableList<NovelCardUIModel> = persistentListOf(),
    val trending: ImmutableList<TrendingReleaseUIModel> = persistentListOf(),
    val statsLabel: UiText? = null,
    /** Left out when the quote request fails; it never makes the screen an error. */
    val quote: QuoteOfTheDayUIModel? = null,
    /** The New & Trending languages from Settings, e.g. "English, Japanese". */
    val trendingLanguagesLabel: UiText? = null,
    val isRolling: Boolean = false,
)
