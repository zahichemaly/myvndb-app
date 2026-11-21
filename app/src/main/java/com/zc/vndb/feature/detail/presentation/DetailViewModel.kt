package com.zc.vndb.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toSpoilerLevel
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.domain.model.Quote
import com.zc.vndb.feature.detail.domain.model.ReleaseSummary
import com.zc.vndb.feature.detail.domain.usecase.GetNovelCastUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelCoversUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelDetailUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelEditionsUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelQuotesUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelReleasesUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelScreenshotsUseCase
import com.zc.vndb.feature.detail.presentation.component.detailhero.mapper.toHeroUIModel
import com.zc.vndb.feature.detail.presentation.component.editionrow.mapper.toRowUIModel
import com.zc.vndb.feature.detail.presentation.component.mylist.mapper.toMyListCard
import com.zc.vndb.feature.detail.presentation.component.relationcard.mapper.toCardUIModel
import com.zc.vndb.feature.detail.presentation.component.taggroups.mapper.toContentNotes
import com.zc.vndb.feature.detail.presentation.component.taggroups.mapper.toHiddenTagsLabel
import com.zc.vndb.feature.detail.presentation.component.taggroups.mapper.toTagGroups
import com.zc.vndb.feature.detail.presentation.mapper.reviewsUrl
import com.zc.vndb.feature.detail.presentation.mapper.toAlsoKnownAs
import com.zc.vndb.feature.detail.presentation.mapper.toCastUIModel
import com.zc.vndb.feature.detail.presentation.mapper.toCoverUIModels
import com.zc.vndb.feature.detail.presentation.mapper.toLinks
import com.zc.vndb.feature.detail.presentation.mapper.toMediaUIModel
import com.zc.vndb.feature.detail.presentation.mapper.toReleaseGroups
import com.zc.vndb.feature.detail.presentation.mapper.toSynopsis
import com.zc.vndb.feature.detail.presentation.mapper.toUIModel
import com.zc.vndb.feature.detail.presentation.mapper.toViewer
import com.zc.vndb.feature.detail.presentation.mapper.vndbUrl
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.CoverRevealRequest
import com.zc.vndb.feature.detail.presentation.model.DetailSection
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import com.zc.vndb.feature.detail.presentation.model.map
import com.zc.vndb.feature.detail.presentation.model.mapItems
import com.zc.vndb.feature.userlist.domain.usecase.GetMyListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.IsNovelFinishedUseCase
import com.zc.vndb.feature.userlist.domain.usecase.ObserveListChangesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

/**
 * Only the Overview fields load on open. Every other section loads the first time it is needed
 * (its tab opens, or its Overview preview scrolls into view) through its own [LazySection], which
 * keeps what it loaded and has its own Retry. The spoiler level and the screenshot toggles only
 * re-map data that is already loaded.
 */
@KoinViewModel
class DetailViewModel(
    @InjectedParam private val novelId: String,
    private val getNovelDetail: GetNovelDetailUseCase,
    getNovelEditions: GetNovelEditionsUseCase,
    getNovelCast: GetNovelCastUseCase,
    getNovelScreenshots: GetNovelScreenshotsUseCase,
    getNovelCovers: GetNovelCoversUseCase,
    getNovelReleases: GetNovelReleasesUseCase,
    getNovelQuotes: GetNovelQuotesUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val observeLanguages: ObserveLanguagesUseCase,
    private val isNovelFinished: IsNovelFinishedUseCase,
    private val observeSession: ObserveSessionUseCase,
    private val getMyListEntry: GetMyListEntryUseCase,
    private val observeListChanges: ObserveListChangesUseCase,
) : ViewModel() {

    private val editionsLoader = SectionLoader(
        scope = viewModelScope,
        fetch = { getNovelEditions(novelId) },
    ) { render(DetailSection.Editions) }
    private val castLoader = SectionLoader(
        scope = viewModelScope,
        fetch = { getNovelCast(novelId) },
    ) { render(DetailSection.Cast) }
    private val screenshotsLoader = SectionLoader(
        scope = viewModelScope,
        fetch = { getNovelScreenshots(novelId) },
    ) { render(DetailSection.Screenshots) }
    private val coversLoader = SectionLoader(
        scope = viewModelScope,
        fetch = { getNovelCovers(novelId) },
    ) { render(DetailSection.Covers) }
    private val releasesLoader = PagedSectionLoader(
        scope = viewModelScope,
        fetch = { page -> getNovelReleases(novelId, page) },
        key = ReleaseSummary::id,
    ) { render(DetailSection.Releases) }
    private val quotesLoader = PagedSectionLoader(
        scope = viewModelScope,
        fetch = { page -> getNovelQuotes(novelId, page) },
        key = Quote::id,
    ) { render(DetailSection.Quotes) }

    private val _state = MutableStateFlow(DetailState(novelId = novelId).withTabs())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = Channel<DetailEffect>(Channel.BUFFERED)
    val effect: Flow<DetailEffect> = _effect.receiveAsFlow()

    // The Overview's domain data and the preferences it was mapped with, kept to re-map it.
    private var novel: NovelDetail? = null
    private var settings = AppSettings()
    private var contentFilters: Set<ContentFilter> = ContentFilter.Default

    /** VNDB's language names for the hero's language rows; the built-in list until one is cached. */
    private var languageOptions: List<LanguageOption> = emptyList()

    /** The page's starting state from Settings is applied once, on the first successful load. */
    private var isStartApplied = false

    /** The reader picked a spoiler level here, so "Spoil me if finished" must not override it. */
    private var hasChosenSpoilerLevel = false

    private var novelJob: Job? = null
    private var myListJob: Job? = null
    private var canWriteList = false

    init {
        onIntent(DetailIntent.Load)
        observeMyList()
    }

    @Suppress("CyclomaticComplexMethod") // A flat dispatch over the screen's intents.
    fun onIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Load, DetailIntent.Retry -> loadNovel()

            DetailIntent.BackClicked -> sendEffect(DetailEffect.NavigateBack)

            DetailIntent.ShareClicked -> sendEffect(
                DetailEffect.Share(novel?.title.orEmpty(), vndbUrl(novelId)),
            )

            DetailIntent.OpenInBrowserClicked -> sendEffect(DetailEffect.OpenUrl(vndbUrl(novelId)))

            DetailIntent.ReviewsClicked -> sendEffect(DetailEffect.OpenUrl(reviewsUrl(novelId)))

            is DetailIntent.LinkClicked -> sendEffect(DetailEffect.OpenUrl(intent.url))

            is DetailIntent.SpoilerLevelSelected -> {
                hasChosenSpoilerLevel = true
                _state.update { it.copy(spoilerLevel = intent.level) }
                renderSpoilerSections()
            }

            DetailIntent.MyListClicked -> sendEffect(
                DetailEffect.OpenListEditor(novelId, novel?.title ?: novelId),
            )

            DetailIntent.SynopsisToggled -> _state.update {
                it.copy(isSynopsisExpanded = !it.isSynopsisExpanded)
            }

            is DetailIntent.RelationClicked -> sendEffect(DetailEffect.NavigateToNovel(intent.id))

            is DetailIntent.CharacterClicked -> sendEffect(
                DetailEffect.NavigateToCharacter(intent.id),
            )

            is DetailIntent.TagClicked -> sendEffect(DetailEffect.NavigateToTag(intent.id))

            is DetailIntent.DeveloperClicked -> sendEffect(
                DetailEffect.NavigateToProducer(intent.id),
            )

            is DetailIntent.StaffClicked -> sendEffect(DetailEffect.NavigateToStaff(intent.id))

            is DetailIntent.EditionClicked -> sendEffect(
                DetailEffect.NavigateToRelease(intent.releaseId),
            )

            is DetailIntent.TabSelected -> _state.update { it.copy(selectedTab = intent.tab) }

            is DetailIntent.SectionNeeded -> loaderFor(intent.section).loadOnce()

            is DetailIntent.SectionRetried -> loaderFor(intent.section).load()

            is DetailIntent.LoadMoreClicked -> when (intent.section) {
                DetailSection.Releases -> releasesLoader.loadMore()
                DetailSection.Quotes -> quotesLoader.loadMore()
                else -> Unit
            }

            is DetailIntent.ContentFilterToggled -> toggleContentFilter(intent.filter)

            is DetailIntent.ScreenshotClicked,
            is DetailIntent.CoverClicked,
            DetailIntent.ViewerDismissed,
            is DetailIntent.CoverRevealRequested,
            DetailIntent.CoverRevealConfirmed,
            DetailIntent.CoverRevealDismissed,
            -> onMediaIntent(intent)
        }
    }

    /** The full-screen viewer and the "I'm 18 or older" question behind a blurred cover. */
    private fun onMediaIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.ScreenshotClicked -> {
                val viewer = screenshotsLoader.data.orEmpty().toViewer(contentFilters, intent.index)
                _state.update { it.copy(viewer = viewer) }
            }

            is DetailIntent.CoverClicked -> _state.update {
                it.copy(viewer = coversLoader.data.orEmpty().toViewer(intent.index))
            }

            DetailIntent.ViewerDismissed -> _state.update { it.copy(viewer = null) }

            is DetailIntent.CoverRevealRequested -> _state.update {
                it.copy(coverReveal = CoverRevealRequest(intent.viewerIndex))
            }

            DetailIntent.CoverRevealConfirmed -> confirmCoverReveal()

            DetailIntent.CoverRevealDismissed -> _state.update { it.copy(coverReveal = null) }

            else -> Unit
        }
    }

    private fun loadNovel() {
        novelJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        novelJob = viewModelScope.launch {
            settings = observeSettings.current()
            languageOptions = observeLanguages().first()
            getNovelDetail(novelId)
                .onSuccess { loaded ->
                    novel = loaded
                    applyStartSettings()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hero = loaded.toHeroUIModel(
                                editionsLoader.data?.ageRange,
                                settings.titleLanguage,
                                languageOptions,
                            ),
                            links = loaded.toLinks(),
                            alsoKnownAs = loaded.toAlsoKnownAs(),
                            contentNotes = loaded.toContentNotes(),
                            relations = loaded.relations.map { relation ->
                                relation.toCardUIModel(settings.titleLanguage)
                            }.toImmutableList(),
                        )
                    }
                    renderSpoilerSections()
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toErrorUIModel()) }
                }
        }
    }

    /**
     * The signed-in user's entry: loaded on sign-in and each time it is edited in the app; hidden
     * for guests. A failed load just leaves the card out.
     */
    private fun observeMyList() {
        viewModelScope.launch {
            observeSession().collect { session ->
                if (session == null) {
                    myListJob?.cancel()
                    _state.update { it.copy(myList = null) }
                } else {
                    canWriteList = session.canWriteList
                    loadMyList()
                }
            }
        }
        viewModelScope.launch {
            observeListChanges().filter { it == novelId }.collect {
                loadMyList()
            }
        }
    }

    private fun loadMyList() {
        myListJob?.cancel()
        myListJob = viewModelScope.launch {
            getMyListEntry(novelId).onSuccess { entry ->
                _state.update { it.copy(myList = entry.toMyListCard(canWriteList)) }
            }
        }
    }

    /**
     * Starting spoiler level and screenshot levels from Settings; then, if "Spoil me if finished"
     * is on and the VN is on the user's Finished list, every spoiler is shown.
     */
    private fun applyStartSettings() {
        if (isStartApplied) return
        isStartApplied = true
        contentFilters =
            if (settings.showSensitiveContent) ContentFilter.entries.toSet() else ContentFilter.Default
        _state.update { it.copy(spoilerLevel = settings.defaultSpoilerLevel.toSpoilerLevel()) }
        if (settings.spoilIfFinished && settings.defaultSpoilerLevel != SpoilerPreference.Major) {
            viewModelScope.launch {
                if (isNovelFinished(novelId) && !hasChosenSpoilerLevel) {
                    _state.update { it.copy(spoilerLevel = SpoilerLevel.Major) }
                    renderSpoilerSections()
                }
            }
        }
    }

    private fun loaderFor(section: DetailSection): LazySection = when (section) {
        DetailSection.Editions -> editionsLoader
        DetailSection.Cast -> castLoader
        DetailSection.Screenshots -> screenshotsLoader
        DetailSection.Covers -> coversLoader
        DetailSection.Releases -> releasesLoader
        DetailSection.Quotes -> quotesLoader
    }

    /** Maps one section's loader into the State, with the tab counts that depend on it. */
    private fun render(section: DetailSection) {
        _state.update { state ->
            when (section) {
                DetailSection.Editions -> state.copy(
                    // The age chip pops in once the releases are known.
                    hero = editionsLoader.data?.let { editions ->
                        novel?.toHeroUIModel(
                            editions.ageRange,
                            settings.titleLanguage,
                            languageOptions,
                        )
                    } ?: state.hero,
                    editions = editionsLoader.state.map { loaded ->
                        loaded.editions.map { it.toRowUIModel() }.toImmutableList()
                    },
                )

                DetailSection.Cast -> state.copy(
                    cast = castLoader.state.map { it.toCastUIModel(state.spoilerLevel) },
                )

                DetailSection.Screenshots -> state.copy(
                    screenshots = screenshotsLoader.state.map { it.toMediaUIModel(contentFilters) },
                )

                DetailSection.Covers -> state.copy(
                    covers = coversLoader.state.map { it.toCoverUIModels() },
                )

                DetailSection.Releases -> state.copy(
                    releases = releasesLoader.state.map { page ->
                        page.mapItems { it.toReleaseGroups() }
                    },
                )

                DetailSection.Quotes -> state.copy(
                    quotes = quotesLoader.state.map { page ->
                        page.mapItems { quotes -> quotes.map { it.toUIModel() } }
                    },
                )
            }.withTabs()
        }
    }

    private fun toggleContentFilter(filter: ContentFilter) {
        contentFilters =
            if (filter in contentFilters) contentFilters - filter else contentFilters + filter
        render(DetailSection.Screenshots)
    }

    /** Overview sections and the cast that depend on the spoiler level. */
    private fun renderSpoilerSections() {
        _state.update { state ->
            val level = state.spoilerLevel
            val current = novel
            state.copy(
                synopsis = current?.toSynopsis(level),
                tagGroups = current?.toTagGroups(level, settings.showSensitiveContent)
                    ?: state.tagGroups,
                hiddenTagsLabel = current?.toHiddenTagsLabel(level, settings.showSensitiveContent),
            )
        }
        render(DetailSection.Cast)
    }

    /** Tab labels, with counts once each tab's whole list is known. */
    private fun DetailState.withTabs() = copy(
        tabs = DetailTab.entries.map { tab ->
            tab.toUIModel(
                count = when (tab) {
                    DetailTab.Overview -> null

                    DetailTab.Media -> screenshots.dataOrNull?.totalCount

                    DetailTab.Cast -> cast.dataOrNull?.characterCount

                    // Releases are shown in groups; the tab counts the releases themselves.
                    DetailTab.Releases -> releases.dataOrNull?.takeIf { !it.hasMore }
                        ?.let { releasesLoader.items.size }

                    DetailTab.Quotes -> quotes.dataOrNull?.takeIf { !it.hasMore }?.items?.size
                },
            )
        }.toImmutableList(),
    )

    /** Shows the page's blurred covers; a cover opened from the carousel goes on to the viewer. */
    private fun confirmCoverReveal() {
        val viewerIndex = _state.value.coverReveal?.viewerIndex
        _state.update {
            it.copy(
                areCoversRevealed = true,
                coverReveal = null,
                viewer = viewerIndex?.let { index ->
                    coversLoader.data.orEmpty().toViewer(index)
                } ?: it.viewer,
            )
        }
    }

    private fun sendEffect(effect: DetailEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
