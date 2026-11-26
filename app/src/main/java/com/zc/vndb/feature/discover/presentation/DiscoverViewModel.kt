package com.zc.vndb.feature.discover.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.feature.discover.domain.model.DiscoverNovel
import com.zc.vndb.feature.discover.domain.usecase.GetDatabaseStatsUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetFeaturedNovelUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetQuoteOfTheDayUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetTopRatedNovelsUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetTrendingReleasesUseCase
import com.zc.vndb.feature.discover.domain.usecase.RollRandomNovelUseCase
import com.zc.vndb.feature.discover.presentation.component.featurednovel.mapper.toUIModel
import com.zc.vndb.feature.discover.presentation.component.novelcard.mapper.toNovelCardUIModel
import com.zc.vndb.feature.discover.presentation.component.quoteoftheday.mapper.toUIModel
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.mapper.toUIModel
import com.zc.vndb.feature.discover.presentation.mapper.toCountLabel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DiscoverViewModel(
    private val getTopRatedNovels: GetTopRatedNovelsUseCase,
    private val getFeaturedNovel: GetFeaturedNovelUseCase,
    private val getTrendingReleases: GetTrendingReleasesUseCase,
    private val getDatabaseStats: GetDatabaseStatsUseCase,
    private val getQuoteOfTheDay: GetQuoteOfTheDayUseCase,
    private val rollRandomNovel: RollRandomNovelUseCase,
    private val observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    private val _effect = Channel<DiscoverEffect>(Channel.BUFFERED)
    val effect: Flow<DiscoverEffect> = _effect.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        onIntent(DiscoverIntent.Load)
    }

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.Load,
            DiscoverIntent.Retry,
            -> load()

            DiscoverIntent.Refresh -> refresh()

            DiscoverIntent.RandomRollClicked -> roll()

            is DiscoverIntent.NovelClicked -> sendEffect(DiscoverEffect.NavigateToNovel(intent.id))

            is DiscoverIntent.ReleaseClicked -> sendEffect(
                DiscoverEffect.NavigateToRelease(intent.id),
            )

            DiscoverIntent.SeeAllTopRatedClicked -> sendEffect(DiscoverEffect.NavigateToTopRated)

            DiscoverIntent.StatsClicked -> sendEffect(DiscoverEffect.NavigateToBrowse)
        }
    }

    /**
     * Loads every section in parallel. Only a Top Rated failure is a screen error (nothing useful
     * to show); a failed hero, trending list or stats line just leaves that section out.
     */
    private fun load() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, isRefreshing = false, error = null) }
        loadJob = viewModelScope.launch { fetch(isRefresh = false) }
    }

    /**
     * Reloads every section behind the pull-to-refresh indicator. With nothing loaded yet (first
     * load in flight or failed) it is a plain load; a failed refresh keeps what is shown.
     */
    private fun refresh() {
        val current = _state.value
        if (current.isRefreshing || current.isLoading) return
        if (current.error != null) return load()
        loadJob?.cancel()
        _state.update { it.copy(isRefreshing = true) }
        loadJob = viewModelScope.launch { fetch(isRefresh = true) }
    }

    private suspend fun fetch(isRefresh: Boolean) = coroutineScope {
        val settings = observeSettings.current()
        val trending = async { getTrendingReleases(settings.trendingLanguages) }
        val stats = async { getDatabaseStats() }
        val quote = async { getQuoteOfTheDay() }
        val topRated = getTopRatedNovels().getOrElse { error ->
            trending.cancel()
            stats.cancel()
            quote.cancel()
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = false) }
                sendEffect(DiscoverEffect.ShowError(error.toErrorUIModel()))
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.toErrorUIModel(),
                        featured = null,
                        topRated = persistentListOf(),
                        trending = persistentListOf(),
                        statsLabel = null,
                        quote = null,
                    )
                }
            }
            return@coroutineScope
        }
        val featured = getFeaturedNovel(topRated).getOrNull()
        val trendingRows = trending.await().getOrNull().orEmpty()
        val statsLabel = stats.await().getOrNull()?.toCountLabel()
        val newQuote = quote.await().getOrNull()?.toUIModel(settings.titleLanguage)
        _state.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                featured = featured?.toUIModel(settings.titleLanguage),
                topRated = topRated.toTopRatedCards(settings.titleLanguage),
                trending = trendingRows.map { row ->
                    row.toUIModel(
                        settings.titleLanguage,
                    )
                }.toImmutableList(),
                trendingLanguagesLabel = settings.trendingLanguages.toLanguagesLabel(),
                statsLabel = statsLabel,
                // A refresh that fails to fetch a new quote keeps the one on screen.
                quote = newQuote ?: it.quote.takeIf { isRefresh },
            )
        }
    }

    private fun roll() {
        if (_state.value.isRolling) return
        _state.update { it.copy(isRolling = true) }
        viewModelScope.launch {
            rollRandomNovel()
                .onSuccess { id -> sendEffect(DiscoverEffect.NavigateToNovel(id)) }
                .onFailure { error -> sendEffect(DiscoverEffect.ShowError(error.toErrorUIModel())) }
            _state.update { it.copy(isRolling = false) }
        }
    }

    private fun sendEffect(effect: DiscoverEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun List<DiscoverNovel>.toTopRatedCards(titleLanguage: TitleLanguage) =
        take(TOP_RATED_ROW_SIZE).map { it.toNovelCardUIModel(titleLanguage) }.toImmutableList()

    private companion object {
        /** The row shows the best few; the whole pool only feeds the weekly pick. */
        const val TOP_RATED_ROW_SIZE = 10
    }
}
