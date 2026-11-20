package com.zc.vndb.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toSpoilerLevel
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.CoverArt
import com.zc.vndb.feature.detail.domain.model.NovelCast
import com.zc.vndb.feature.detail.domain.model.NovelDetail
import com.zc.vndb.feature.detail.domain.model.NovelEditions
import com.zc.vndb.feature.detail.domain.model.Quote
import com.zc.vndb.feature.detail.domain.model.ReleaseSummary
import com.zc.vndb.feature.detail.domain.model.ResultPage
import com.zc.vndb.feature.detail.domain.model.Screenshot
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
import com.zc.vndb.feature.detail.presentation.model.DetailTabUIModel
import com.zc.vndb.feature.detail.presentation.model.PagedListUIState
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import com.zc.vndb.feature.profile.domain.usecase.GetMyListEntryUseCase
import com.zc.vndb.feature.profile.domain.usecase.IsNovelFinishedUseCase
import com.zc.vndb.feature.profile.domain.usecase.ObserveListChangesUseCase
import com.zc.vndb.feature.profile.domain.usecase.ObserveSessionUseCase
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
 * (its tab opens, or its Overview preview scrolls into view), is cached here, and has its own Retry.
 * The spoiler level and the screenshot toggles only re-filter data that is already loaded.
 */
@Suppress("TooManyFunctions") // One small loader per lazily loaded section keeps each request isolated.
@KoinViewModel
class DetailViewModel(
    @InjectedParam private val novelId: String,
    private val getNovelDetail: GetNovelDetailUseCase,
    private val getNovelEditions: GetNovelEditionsUseCase,
    private val getNovelCast: GetNovelCastUseCase,
    private val getNovelScreenshots: GetNovelScreenshotsUseCase,
    private val getNovelCovers: GetNovelCoversUseCase,
    private val getNovelReleases: GetNovelReleasesUseCase,
    private val getNovelQuotes: GetNovelQuotesUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val observeLanguages: ObserveLanguagesUseCase,
    private val isNovelFinished: IsNovelFinishedUseCase,
    private val observeSession: ObserveSessionUseCase,
    private val getMyListEntry: GetMyListEntryUseCase,
    private val observeListChanges: ObserveListChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        DetailState(novelId = novelId).let { it.copy(tabs = tabsFor(it)) },
    )
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = Channel<DetailEffect>(Channel.BUFFERED)
    val effect: Flow<DetailEffect> = _effect.receiveAsFlow()

    // Loaded domain data, kept to re-map when filters change.
    private var novel: NovelDetail? = null
    private var editions: NovelEditions? = null
    private var cast: NovelCast? = null
    private var screenshots: List<Screenshot> = emptyList()
    private var covers: List<CoverArt> = emptyList()
    private var releases: List<ReleaseSummary> = emptyList()
    private var quotes: List<Quote> = emptyList()
    private var contentFilters: Set<ContentFilter> = ContentFilter.Default
    private var settings = AppSettings()

    /** VNDB's language names for the hero's language rows; the built-in list until one is cached. */
    private var languageOptions: List<LanguageOption> = emptyList()

    /** The page's starting state from Settings is applied once, on the first successful load. */
    private var isStartApplied = false

    /** The reader picked a spoiler level here, so "Spoil me if finished" must not override it. */
    private var hasChosenSpoilerLevel = false

    private var novelJob: Job? = null
    private val sectionJobs = mutableMapOf<DetailSection, Job>()
    private val nextPage = mutableMapOf(
        DetailSection.Releases to FIRST_PAGE,
        DetailSection.Quotes to FIRST_PAGE,
    )

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
                it.copy(
                    isSynopsisExpanded = !it.isSynopsisExpanded,
                )
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

            is DetailIntent.SectionNeeded -> if (sectionState(
                    intent.section,
                ) == SectionState.Idle
            ) {
                loadSection(intent.section)
            }

            is DetailIntent.SectionRetried -> loadSection(intent.section)

            is DetailIntent.LoadMoreClicked -> loadNextPage(intent.section)

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
            is DetailIntent.ScreenshotClicked -> _state.update {
                it.copy(viewer = screenshots.toViewer(contentFilters, intent.index))
            }

            is DetailIntent.CoverClicked -> _state.update {
                it.copy(viewer = covers.toViewer(intent.index))
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
                                editions?.ageRange,
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

    private fun loadSection(section: DetailSection) {
        if (sectionJobs[section]?.isActive == true) return
        setSection(section, SectionState.Loading)
        sectionJobs[section] = viewModelScope.launch {
            when (section) {
                DetailSection.Editions -> getNovelEditions(
                    novelId,
                ).fold(::onEditionsLoaded) { fail(section, it) }

                DetailSection.Cast -> getNovelCast(
                    novelId,
                ).fold(::onCastLoaded) { fail(section, it) }

                DetailSection.Screenshots -> getNovelScreenshots(
                    novelId,
                ).fold(::onScreenshotsLoaded) { fail(section, it) }

                DetailSection.Covers -> getNovelCovers(
                    novelId,
                ).fold(::onCoversLoaded) { fail(section, it) }

                DetailSection.Releases -> {
                    nextPage[section] = FIRST_PAGE
                    getNovelReleases(
                        novelId,
                        FIRST_PAGE,
                    ).fold({ onReleasesPage(it, isFirst = true) }) { fail(section, it) }
                }

                DetailSection.Quotes -> {
                    nextPage[section] = FIRST_PAGE
                    getNovelQuotes(
                        novelId,
                        FIRST_PAGE,
                    ).fold({ onQuotesPage(it, isFirst = true) }) { fail(section, it) }
                }
            }
        }
    }

    private fun onEditionsLoaded(loaded: NovelEditions) {
        editions = loaded
        _state.update { state ->
            state.copy(
                // The age chip pops in once the releases are known.
                hero = novel?.toHeroUIModel(
                    loaded.ageRange,
                    settings.titleLanguage,
                    languageOptions,
                )
                    ?: state.hero,
                editions = SectionState.Loaded(
                    loaded.editions.map { it.toRowUIModel() }.toImmutableList(),
                ),
            )
        }
    }

    private fun onCastLoaded(loaded: NovelCast) {
        cast = loaded
        renderSpoilerSections()
        refreshTabs()
    }

    private fun onScreenshotsLoaded(loaded: List<Screenshot>) {
        screenshots = loaded
        _state.update {
            it.copy(
                screenshots = SectionState.Loaded(loaded.toMediaUIModel(contentFilters)),
            )
        }
        refreshTabs()
    }

    private fun onCoversLoaded(loaded: List<CoverArt>) {
        covers = loaded
        _state.update { it.copy(covers = SectionState.Loaded(loaded.toCoverUIModels())) }
    }

    private fun onReleasesPage(page: ResultPage<ReleaseSummary>, isFirst: Boolean) {
        releases = if (isFirst) page.items else (releases + page.items).distinctBy { it.id }
        nextPage[DetailSection.Releases] = nextPage.getValue(DetailSection.Releases) + 1
        _state.update {
            it.copy(
                releases = SectionState.Loaded(
                    PagedListUIState(releases.toReleaseGroups(), hasMore = page.hasMore),
                ),
            )
        }
        refreshTabs()
    }

    private fun onQuotesPage(page: ResultPage<Quote>, isFirst: Boolean) {
        quotes = if (isFirst) page.items else (quotes + page.items).distinctBy { it.id }
        nextPage[DetailSection.Quotes] = nextPage.getValue(DetailSection.Quotes) + 1
        _state.update {
            it.copy(
                quotes = SectionState.Loaded(
                    PagedListUIState(
                        quotes.map { quote -> quote.toUIModel() }.toImmutableList(),
                        hasMore = page.hasMore,
                    ),
                ),
            )
        }
        refreshTabs()
    }

    /** Next page of Releases or Quotes; a failure keeps what is shown and offers "Load more" again. */
    private fun loadNextPage(section: DetailSection) {
        if (sectionJobs[section]?.isActive == true) return
        val page = nextPage[section] ?: return
        setLoadingMore(section, isLoading = true, failed = false)
        sectionJobs[section] = viewModelScope.launch {
            val result = when (section) {
                DetailSection.Releases -> getNovelReleases(
                    novelId,
                    page,
                ).map { onReleasesPage(it, isFirst = false) }

                DetailSection.Quotes -> getNovelQuotes(
                    novelId,
                    page,
                ).map { onQuotesPage(it, isFirst = false) }

                else -> return@launch
            }
            result.onFailure { setLoadingMore(section, isLoading = false, failed = true) }
        }
    }

    private fun toggleContentFilter(filter: ContentFilter) {
        contentFilters =
            if (filter in contentFilters) contentFilters - filter else contentFilters + filter
        if (_state.value.screenshots is SectionState.Loaded) {
            _state.update {
                it.copy(
                    screenshots = SectionState.Loaded(screenshots.toMediaUIModel(contentFilters)),
                )
            }
        }
    }

    /** Overview sections and the cast that depend on the spoiler level. */
    private fun renderSpoilerSections() {
        val level = _state.value.spoilerLevel
        _state.update { state ->
            val current = novel
            state.copy(
                synopsis = current?.toSynopsis(level),
                tagGroups = current?.toTagGroups(
                    level,
                    settings.showSensitiveContent,
                ) ?: state.tagGroups,
                hiddenTagsLabel = current?.toHiddenTagsLabel(level, settings.showSensitiveContent),
                cast = cast?.let { SectionState.Loaded(it.toCastUIModel(level)) } ?: state.cast,
            )
        }
    }

    private fun refreshTabs() {
        _state.update { it.copy(tabs = tabsFor(it)) }
    }

    /** Tab labels, with counts once each tab's data is known. */
    private fun tabsFor(state: DetailState) = DetailTab.entries.map { tab ->
        val count = when (tab) {
            DetailTab.Overview -> null

            DetailTab.Media -> state.screenshots.dataOrNull?.totalCount

            DetailTab.Cast -> state.cast.dataOrNull?.characterCount

            DetailTab.Releases -> state.releases.dataOrNull?.takeIf {
                !it.hasMore
            }?.let { releases.size }

            DetailTab.Quotes -> state.quotes.dataOrNull?.takeIf { !it.hasMore }?.items?.size
        }
        val name = tab.toLabel()
        DetailTabUIModel(
            tab = tab,
            label = count?.let {
                UiText.StringResource(
                    StringResource.detail_tab_with_count,
                    listOf(name, it),
                )
            } ?: name,
        )
    }.toImmutableList()

    private fun sectionState(section: DetailSection): SectionState<*> = with(_state.value) {
        when (section) {
            DetailSection.Editions -> editions
            DetailSection.Cast -> cast
            DetailSection.Screenshots -> screenshots
            DetailSection.Covers -> covers
            DetailSection.Releases -> releases
            DetailSection.Quotes -> quotes
        }
    }

    private fun setSection(section: DetailSection, value: SectionState<Nothing>) {
        _state.update {
            when (section) {
                DetailSection.Editions -> it.copy(editions = value)
                DetailSection.Cast -> it.copy(cast = value)
                DetailSection.Screenshots -> it.copy(screenshots = value)
                DetailSection.Covers -> it.copy(covers = value)
                DetailSection.Releases -> it.copy(releases = value)
                DetailSection.Quotes -> it.copy(quotes = value)
            }
        }
    }

    private fun fail(section: DetailSection, error: Throwable) {
        setSection(section, SectionState.Failed(error.toErrorUIModel()))
    }

    private fun setLoadingMore(section: DetailSection, isLoading: Boolean, failed: Boolean) {
        _state.update { state ->
            when (section) {
                DetailSection.Releases -> state.copy(
                    releases = state.releases.dataOrNull
                        ?.let {
                            SectionState.Loaded(
                                it.copy(isLoadingMore = isLoading, loadMoreFailed = failed),
                            )
                        }
                        ?: state.releases,
                )

                DetailSection.Quotes -> state.copy(
                    quotes = state.quotes.dataOrNull
                        ?.let {
                            SectionState.Loaded(
                                it.copy(isLoadingMore = isLoading, loadMoreFailed = failed),
                            )
                        }
                        ?: state.quotes,
                )

                else -> state
            }
        }
    }

    /** Shows the page's blurred covers; a cover opened from the carousel goes on to the viewer. */
    private fun confirmCoverReveal() {
        val viewerIndex = _state.value.coverReveal?.viewerIndex
        _state.update {
            it.copy(
                areCoversRevealed = true,
                coverReveal = null,
                viewer = viewerIndex?.let { index -> covers.toViewer(index) } ?: it.viewer,
            )
        }
    }

    private fun sendEffect(effect: DetailEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun DetailTab.toLabel(): UiText = UiText.StringResource(
        when (this) {
            DetailTab.Overview -> StringResource.detail_tab_overview
            DetailTab.Media -> StringResource.detail_tab_media
            DetailTab.Cast -> StringResource.detail_tab_cast
            DetailTab.Releases -> StringResource.detail_tab_releases
            DetailTab.Quotes -> StringResource.detail_tab_quotes
        },
    )

    private companion object {
        const val FIRST_PAGE = 1
    }
}
