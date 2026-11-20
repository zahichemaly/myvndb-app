package com.zc.vndb.feature.browse.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.isDescending
import com.zc.vndb.feature.browse.domain.usecase.GetProducerUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetStaffUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetTagUseCase
import com.zc.vndb.feature.browse.domain.usecase.SearchNovelsUseCase
import com.zc.vndb.feature.browse.presentation.component.browsenovelcard.mapper.toCardUIModel
import com.zc.vndb.feature.browse.presentation.component.scopeheader.mapper.toHeaderUIModel
import com.zc.vndb.feature.browse.presentation.mapper.toGenreChips
import com.zc.vndb.feature.browse.presentation.mapper.toResultCountLabel
import com.zc.vndb.feature.browse.presentation.mapper.toSortOptions
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class BrowseViewModel(
    /** What the list is limited to: [BrowseScope.None] for the Browse tab. */
    @InjectedParam private val scope: BrowseScope,
    private val searchNovels: SearchNovelsUseCase,
    private val getTag: GetTagUseCase,
    private val getProducer: GetProducerUseCase,
    private val getStaff: GetStaffUseCase,
    private val observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseState(isScopedPage = scope != BrowseScope.None))
    val state: StateFlow<BrowseState> = _state.asStateFlow()

    private val _effect = Channel<BrowseEffect>(Channel.BUFFERED)
    val effect: Flow<BrowseEffect> = _effect.receiveAsFlow()

    /** The filter the current results belong to. */
    private var filter = BrowseFilter(scope = scope, sort = scope.defaultSort)
    private var nextPage = SearchNovelsUseCase.FIRST_PAGE
    private var loadJob: Job? = null
    private var debounceJob: Job? = null
    private var titleLanguage = TitleLanguage.Romanized

    // The starting age rating comes from Settings ("Include 18+ titles in Browse").
    init {
        loadHeader()
        viewModelScope.launch {
            val settings = observeSettings.current()
            filter = filter.copy(
                ageRating = if (settings.includeAdultInBrowse) AgeRating.Any else AgeRating.AllAges,
            )
            onIntent(BrowseIntent.Load)
        }
    }

    fun onIntent(intent: BrowseIntent) {
        when (intent) {
            BrowseIntent.Load,
            BrowseIntent.Retry,
            -> reload()

            BrowseIntent.Refresh -> refresh()

            is BrowseIntent.QueryChanged -> onQueryChanged(intent.query)

            BrowseIntent.SearchSubmitted -> applyFilter(filter.withQuery(_state.value.query))

            BrowseIntent.QueryCleared -> clearQuery()

            is BrowseIntent.AgeRatingSelected -> applyFilter(
                filter.copy(ageRating = intent.ageRating),
            )

            is BrowseIntent.GenreToggled -> toggleGenre(intent.genre)

            // A newly picked sort starts in its own natural direction.
            is BrowseIntent.SortSelected -> applyFilter(
                filter.copy(sort = intent.sort, isReversed = false),
            )

            BrowseIntent.SortDirectionToggled -> applyFilter(
                filter.copy(isReversed = !filter.isReversed),
            )

            BrowseIntent.EndReached -> onEndReached()

            BrowseIntent.LoadMoreRetried -> loadNextPage()

            is BrowseIntent.NovelClicked,
            BrowseIntent.BackClicked,
            is BrowseIntent.LinkClicked,
            BrowseIntent.DescriptionToggled,
            -> onPageIntent(intent)
        }
    }

    /** Taps on the page itself: navigation, links and the description. */
    private fun onPageIntent(intent: BrowseIntent) {
        when (intent) {
            is BrowseIntent.NovelClicked -> sendEffect(BrowseEffect.NavigateToNovel(intent.id))

            BrowseIntent.BackClicked -> sendEffect(BrowseEffect.NavigateBack)

            is BrowseIntent.LinkClicked -> sendEffect(BrowseEffect.OpenUrl(intent.url))

            BrowseIntent.DescriptionToggled ->
                _state.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }

            else -> Unit
        }
    }

    private fun clearQuery() {
        _state.update { it.copy(query = "") }
        applyFilter(filter.withQuery(""))
    }

    private fun toggleGenre(genre: Genre) {
        val genres = if (genre in filter.genres) filter.genres - genre else filter.genres + genre
        applyFilter(filter.copy(genres = genres))
    }

    /** After a failed page, wait for an explicit retry instead of re-requesting on every scroll. */
    private fun onEndReached() {
        if (!_state.value.loadMoreFailed) loadNextPage()
    }

    /** The header is optional: if it fails to load, the list still works (and keeps the old one). */
    private fun loadHeader() {
        if (scope == BrowseScope.None) return
        _state.update { it.copy(isHeaderLoading = true) }
        viewModelScope.launch {
            val header = when (val scope = scope) {
                BrowseScope.None -> null
                is BrowseScope.Tag -> getTag(scope.id).map { it.toHeaderUIModel() }
                is BrowseScope.Producer -> getProducer(scope.id).map { it.toHeaderUIModel() }
                is BrowseScope.Staff -> getStaff(scope.id).map { it.toHeaderUIModel() }
            }?.getOrNull()
            _state.update { it.copy(header = header ?: it.header, isHeaderLoading = false) }
        }
    }

    private fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            applyFilter(filter.withQuery(query))
        }
    }

    private fun applyFilter(newFilter: BrowseFilter) {
        debounceJob?.cancel()
        if (newFilter == filter) return
        filter = newFilter
        reload()
    }

    /** Starts over from the first page of [filter]. */
    private fun reload() {
        loadJob?.cancel()
        nextPage = SearchNovelsUseCase.FIRST_PAGE
        _state.update {
            it.copy(
                ageRating = filter.ageRating,
                sort = filter.sort,
                isSortAscending = !filter.isDescending,
                sortOptions = filter.toSortOptions(),
                genres = filter.toGenreChips(),
                results = persistentListOf(),
                resultCountLabel = null,
                isLoading = true,
                isRefreshing = false,
                isLoadingMore = false,
                canLoadMore = false,
                loadMoreFailed = false,
                error = null,
            )
        }
        loadJob = viewModelScope.launch {
            titleLanguage = observeSettings.current().titleLanguage
            searchNovels(filter, nextPage)
                .onSuccess { page ->
                    nextPage++
                    _state.update {
                        it.copy(
                            isLoading = false,
                            results = page.novels.map { novel ->
                                novel.toCardUIModel(titleLanguage)
                            }.toImmutableList(),
                            resultCountLabel = page.totalCount?.toResultCountLabel(),
                            canLoadMore = page.hasMore,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.toErrorUIModel(),
                        )
                    }
                }
        }
    }

    /**
     * Reloads the first page of the current filter (and the header) behind the pull-to-refresh
     * indicator. With no results to keep it is a plain reload; a failed refresh keeps them.
     */
    private fun refresh() {
        val current = _state.value
        if (current.isRefreshing || current.isLoading) return
        if (current.results.isEmpty()) return reload()
        loadJob?.cancel()
        loadHeader()
        _state.update {
            it.copy(
                isRefreshing = true,
                isLoadingMore = false,
                loadMoreFailed = false,
            )
        }
        loadJob = viewModelScope.launch {
            searchNovels(filter, SearchNovelsUseCase.FIRST_PAGE)
                .onSuccess { page ->
                    nextPage = SearchNovelsUseCase.FIRST_PAGE + 1
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            results = page.novels.map { novel ->
                                novel.toCardUIModel(titleLanguage)
                            }.toImmutableList(),
                            resultCountLabel = page.totalCount?.toResultCountLabel(),
                            canLoadMore = page.hasMore,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isRefreshing = false) }
                    sendEffect(BrowseEffect.ShowError(error.toErrorUIModel()))
                }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isBusy || !current.canLoadMore) return
        _state.update { it.copy(isLoadingMore = true, loadMoreFailed = false) }
        loadJob = viewModelScope.launch {
            searchNovels(filter, nextPage)
                .onSuccess { page ->
                    nextPage++
                    _state.update { state ->
                        val known = state.results.mapTo(HashSet()) { it.id }
                        val added = page.novels.filter {
                            it.id !in known
                        }.map { it.toCardUIModel(titleLanguage) }
                        state.copy(
                            isLoadingMore = false,
                            results = (state.results + added).toImmutableList(),
                            canLoadMore = page.hasMore,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            loadMoreFailed = true,
                        )
                    }
                }
        }
    }

    private fun sendEffect(effect: BrowseEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    /**
     * Searching switches the scope's default sort to Relevance; clearing the search
     * switches Relevance back to it.
     */
    private fun BrowseFilter.withQuery(rawQuery: String): BrowseFilter {
        val newQuery = rawQuery.trim()
        val newSort = when {
            newQuery.isNotEmpty() && query.isEmpty() && sort == scope.defaultSort -> BrowseSort.Relevance
            newQuery.isEmpty() && sort == BrowseSort.Relevance -> scope.defaultSort
            else -> sort
        }
        return copy(query = newQuery, sort = newSort, isReversed = isReversed && newSort == sort)
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 400L
    }
}
