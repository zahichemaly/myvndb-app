package com.zc.vndb.feature.discover.presentation.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.feature.discover.domain.model.TrendingRelease
import com.zc.vndb.feature.discover.domain.usecase.GetTrendingReleasesPageUseCase
import com.zc.vndb.feature.discover.presentation.component.trendingrelease.mapper.toUIModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/**
 * Discover's "New & Trending" in full: pages of the newest releases in the user's languages, one
 * row per visual novel, loaded as the list is scrolled.
 */
@KoinViewModel
class TrendingViewModel(
    private val getTrendingPage: GetTrendingReleasesPageUseCase,
    private val observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TrendingState())
    val state: StateFlow<TrendingState> = _state.asStateFlow()

    private val _effect = Channel<TrendingEffect>(Channel.BUFFERED)
    val effect: Flow<TrendingEffect> = _effect.receiveAsFlow()

    private var loadJob: Job? = null

    /** The page to ask for next; null once VNDB has no more. */
    private var nextPage: Int? = null

    /** Visual novels already listed, so a later page doesn't list them again. */
    private var shownNovelIds: Set<String> = emptySet()

    init {
        loadFirstPage(isRefresh = false)
    }

    fun onIntent(intent: TrendingIntent) {
        when (intent) {
            TrendingIntent.Retry -> loadFirstPage(isRefresh = false)

            TrendingIntent.Refresh -> loadFirstPage(isRefresh = true)

            TrendingIntent.EndReached -> if (!_state.value.loadMoreFailed) loadNextPage()

            TrendingIntent.LoadMoreRetried -> loadNextPage()

            is TrendingIntent.NovelClicked -> sendEffect(TrendingEffect.NavigateToNovel(intent.id))

            is TrendingIntent.ReleaseClicked -> sendEffect(
                TrendingEffect.NavigateToRelease(intent.id),
            )

            TrendingIntent.BackClicked -> sendEffect(TrendingEffect.NavigateBack)
        }
    }

    /** From scratch. A refresh keeps the rows on screen until the new ones arrive. */
    private fun loadFirstPage(isRefresh: Boolean) {
        loadJob?.cancel()
        _state.update {
            if (isRefresh) it.copy(isRefreshing = true) else it.copy(isLoading = true, error = null)
        }
        loadJob = viewModelScope.launch {
            val settings = observeSettings.current()
            fetchFrom(page = 1, settings.trendingLanguages, shown = emptySet())
                .onSuccess { loaded ->
                    shownNovelIds = loaded.rows.mapTo(mutableSetOf()) { it.novelId }
                    nextPage = loaded.nextPage
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            languagesLabel = settings.trendingLanguages.toLanguagesLabel(),
                            rows = loaded.rows
                                .map { row -> row.toUIModel(settings.titleLanguage) }
                                .toImmutableList(),
                            isLoadingMore = false,
                            loadMoreFailed = false,
                        )
                    }
                }
                .onFailure { error ->
                    val model = error.toErrorUIModel()
                    if (isRefresh) {
                        _state.update { it.copy(isRefreshing = false) }
                        sendEffect(TrendingEffect.ShowError(model))
                    } else {
                        _state.update { it.copy(isLoading = false, error = model) }
                    }
                }
        }
    }

    private fun loadNextPage() {
        val page = nextPage ?: return
        if (loadJob?.isActive == true) return
        _state.update { it.copy(isLoadingMore = true, loadMoreFailed = false) }
        loadJob = viewModelScope.launch {
            val settings = observeSettings.current()
            fetchFrom(page, settings.trendingLanguages, shownNovelIds)
                .onSuccess { loaded ->
                    shownNovelIds = shownNovelIds + loaded.rows.map { it.novelId }
                    nextPage = loaded.nextPage
                    val newRows = loaded.rows.map { row -> row.toUIModel(settings.titleLanguage) }
                    _state.update {
                        it.copy(rows = (it.rows + newRows).toImmutableList(), isLoadingMore = false)
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingMore = false, loadMoreFailed = true) }
                }
        }
    }

    /**
     * [page], or the next pages after it while they bring no new row (every release on them was
     * machine-translated only or for a visual novel already listed), up to [MAX_PAGES_PER_LOAD]:
     * a load that adds nothing would leave the list's end-reached trigger with nothing to fire on.
     */
    private suspend fun fetchFrom(
        page: Int,
        languages: List<String>,
        shown: Set<String>,
        attempt: Int = 1,
    ): Result<LoadedPage> = getTrendingPage(languages, page, shown).fold(
        onSuccess = { trending ->
            val next = (page + 1).takeIf { trending.hasMore }
            if (trending.rows.isEmpty() && next != null && attempt < MAX_PAGES_PER_LOAD) {
                fetchFrom(next, languages, shown, attempt + 1)
            } else {
                Result.success(LoadedPage(trending.rows, next))
            }
        },
        onFailure = { Result.failure(it) },
    )

    private fun sendEffect(effect: TrendingEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private data class LoadedPage(val rows: List<TrendingRelease>, val nextPage: Int?)

    private companion object {
        const val MAX_PAGES_PER_LOAD = 3
    }
}
