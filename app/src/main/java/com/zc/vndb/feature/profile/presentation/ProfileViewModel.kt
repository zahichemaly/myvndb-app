package com.zc.vndb.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.mapper.resolve
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.profile.domain.model.ListCursor
import com.zc.vndb.feature.profile.domain.model.ListEntry
import com.zc.vndb.feature.profile.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.UserProfile
import com.zc.vndb.feature.profile.domain.usecase.GetListEntriesUseCase
import com.zc.vndb.feature.profile.domain.usecase.GetProfileUseCase
import com.zc.vndb.feature.profile.domain.usecase.ObserveListChangesUseCase
import com.zc.vndb.feature.profile.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.feature.profile.presentation.component.listentrycard.mapper.toCardUIModel
import com.zc.vndb.feature.profile.presentation.component.profileheader.mapper.toHeaderUIModel
import com.zc.vndb.feature.profile.presentation.mapper.pickSelected
import com.zc.vndb.feature.profile.presentation.mapper.toChips
import com.zc.vndb.feature.profile.presentation.mapper.toSortOptions
import com.zc.vndb.feature.profile.presentation.model.EntriesUIState
import com.zc.vndb.feature.settings.domain.usecase.UpdateSettingsUseCase
import kotlin.time.Clock
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/**
 * Follows the stored session: a guest sees the log-in prompt, a signed-in user their labels and
 * the entries of the selected one. Each label's entries load the first time it is selected and are
 * kept; a token VNDB rejects signs the user out (the repository clears it) with a message.
 */
@KoinViewModel
class ProfileViewModel(
    observeSession: ObserveSessionUseCase,
    private val getProfile: GetProfileUseCase,
    private val getListEntries: GetListEntriesUseCase,
    private val clock: Clock,
    private val observeSettings: ObserveSettingsUseCase,
    private val observeLanguages: ObserveLanguagesUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    observeListChanges: ObserveListChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())

    val state: StateFlow<ProfileState> = combine(
        _state,
        observeSession().onEach(::onSession),
    ) { state, _ -> state }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            ProfileState(),
        )

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect: Flow<ProfileEffect> = _effect.receiveAsFlow()

    private var session: Session? = null
    private var isSessionRead = false
    private var user: UserProfile? = null
    private var labels: List<ListLabel> = emptyList()
    private var selectedLabelId: Int? = null
    private val entriesByLabel = mutableMapOf<Int, LabelEntries>()
    private var titleLanguage = TitleLanguage.Romanized

    /** The languages the user chose to flag on cards (Settings), with their names. */
    private var cardLanguages: List<LanguageOption> = emptyList()
    private var listSort = ListSort.Title
    private var isSortReversed = false
    private var overviewJob: Job? = null
    private var entriesJob: Job? = null

    init {
        // The pills only change what the cards show, so the loaded entries are drawn again.
        viewModelScope.launch {
            combine(
                observeSettings().map { it.cardLanguages }.distinctUntilChanged(),
                observeLanguages(),
            ) { codes, options -> options.resolve(codes) }
                .collect { resolved ->
                    cardLanguages = resolved
                    selectedLabelId?.let(::render)
                }
        }
        // An entry edited in the list editor can move between labels: reload what is shown.
        viewModelScope.launch {
            observeListChanges().collect {
                if (session != null && _state.value.isSignedIn) {
                    loadOverview(
                        isRefresh = labels.isNotEmpty(),
                    )
                }
            }
        }
    }

    @Suppress("CyclomaticComplexMethod") // A flat dispatch over the screen's intents.
    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.LogInClicked -> sendEffect(ProfileEffect.NavigateToLogin)

            ProfileIntent.SettingsClicked -> sendEffect(ProfileEffect.NavigateToSettings)

            ProfileIntent.Retry -> loadOverview(isRefresh = false)

            ProfileIntent.Refresh -> refresh()

            is ProfileIntent.LabelSelected -> selectLabel(intent.id)

            ProfileIntent.EndReached -> if (!_state.value.entries.loadMoreFailed) loadNextPage()

            ProfileIntent.LoadMoreRetried -> loadNextPage()

            ProfileIntent.EntriesRetried -> selectedLabelId?.let {
                loadFirstPage(
                    it,
                    isRefresh = false,
                )
            }

            is ProfileIntent.EntryClicked -> sendEffect(
                ProfileEffect.NavigateToNovel(intent.novelId),
            )

            is ProfileIntent.EntryEditClicked -> {
                val title = entriesByLabel.values.asSequence().flatMap { it.entries }
                    .firstOrNull { it.novelId == intent.novelId }?.title ?: intent.novelId
                sendEffect(ProfileEffect.OpenListEditor(intent.novelId, title))
            }

            is ProfileIntent.SortSelected -> selectSort(intent.sort)

            ProfileIntent.SortDirectionToggled -> toggleSortDirection()
        }
    }

    /** A new account (or a guest) starts over; the same account keeps what is loaded. */
    private fun onSession(newSession: Session?) {
        if (isSessionRead && newSession == session) return
        isSessionRead = true
        session = newSession
        overviewJob?.cancel()
        entriesJob?.cancel()
        user = null
        labels = emptyList()
        selectedLabelId = null
        entriesByLabel.clear()
        _state.value = ProfileState(
            isSessionKnown = true,
            header = newSession?.toHeaderUIModel(),
            canEditList = newSession?.canWriteList == true,
        )
        if (newSession != null) loadOverview(isRefresh = false)
    }

    private fun refresh() {
        val current = _state.value
        if (!current.isSignedIn || current.isLoading || current.isRefreshing) return
        if (labels.isEmpty()) return loadOverview(isRefresh = false)
        loadOverview(isRefresh = true)
    }

    /** Labels and profile; then the selected label's first page. */
    private fun loadOverview(isRefresh: Boolean) {
        val current = session ?: return
        overviewJob?.cancel()
        entriesJob?.cancel()
        _state.update { it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null) }
        overviewJob = viewModelScope.launch {
            val settings = observeSettings.current()
            titleLanguage = settings.titleLanguage
            listSort = settings.listSort
            isSortReversed = settings.isListSortReversed
            renderSort()
            getProfile(current.userId)
                .onSuccess { overview ->
                    user = overview.user
                    labels = overview.labels
                    entriesByLabel.clear()
                    selectedLabelId = labels.pickSelected(selectedLabelId)
                    _state.update {
                        it.copy(
                            header = current.toHeaderUIModel(user),
                            isLoading = false,
                            labels = labels.toChips(selectedLabelId),
                        )
                    }
                    val selected = selectedLabelId
                    if (selected != null) {
                        loadFirstPage(selected, isRefresh)
                    } else {
                        _state.update { it.copy(isRefreshing = false, entries = EntriesUIState()) }
                    }
                }
                .onFailure { error ->
                    if (onUnauthorized(error)) return@onFailure
                    val keepContent = isRefresh && labels.isNotEmpty()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = if (keepContent) null else error.toErrorUIModel(),
                        )
                    }
                    if (keepContent) {
                        sendEffect(
                            ProfileEffect.ShowMessage(error.toErrorUIModel().message),
                        )
                    }
                }
        }
    }

    private fun selectLabel(id: Int) {
        if (id == selectedLabelId || labels.none { it.id == id }) return
        entriesJob?.cancel()
        selectedLabelId = id
        _state.update { it.copy(labels = labels.toChips(id)) }
        if (id in entriesByLabel) render(id) else loadFirstPage(id, isRefresh = false)
    }

    /** On refresh the current entries stay on screen until the new page lands. */
    private fun loadFirstPage(labelId: Int, isRefresh: Boolean) {
        val current = session ?: return
        entriesJob?.cancel()
        if (!isRefresh) _state.update { it.copy(entries = EntriesUIState(isLoading = true)) }
        entriesJob = viewModelScope.launch {
            getListEntries(current.userId, labelId, listSort, isSortReversed)
                .onSuccess { batch ->
                    entriesByLabel[labelId] = LabelEntries(
                        entries = batch.entries,
                        next = batch.next,
                    )
                    render(labelId)
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { error ->
                    if (onUnauthorized(error)) return@onFailure
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            entries = EntriesUIState(error = error.toErrorUIModel()),
                        )
                    }
                }
        }
    }

    /** Next page of the selected label; a failure keeps what is shown and offers a retry. */
    private fun loadNextPage() {
        val (userId, labelId, loaded) = nextPageTarget() ?: return
        _state.update {
            it.copy(
                entries = it.entries.copy(isLoadingMore = true, loadMoreFailed = false),
            )
        }
        entriesJob = viewModelScope.launch {
            getListEntries(userId, labelId, listSort, isSortReversed, loaded.next)
                .onSuccess { batch ->
                    entriesByLabel[labelId] = LabelEntries(
                        entries = (loaded.entries + batch.entries).distinctBy { it.novelId },
                        next = batch.next,
                    )
                    render(labelId)
                }
                .onFailure { error ->
                    if (onUnauthorized(error)) return@onFailure
                    _state.update {
                        it.copy(
                            entries = it.entries.copy(isLoadingMore = false, loadMoreFailed = true),
                        )
                    }
                }
        }
    }

    /** Who, which label and what is loaded, when the selected label has another page to fetch. */
    private fun nextPageTarget(): Triple<String, Int, LabelEntries>? {
        val userId = session?.userId
        val labelId = selectedLabelId
        val loaded = labelId?.let(
            entriesByLabel::get,
        )?.takeIf { it.hasMore && entriesJob?.isActive != true }
        return if (userId != null && labelId != null && loaded != null) {
            Triple(
                userId,
                labelId,
                loaded,
            )
        } else {
            null
        }
    }

    private fun render(labelId: Int) {
        if (labelId != selectedLabelId) return
        val loaded = entriesByLabel[labelId] ?: return
        val now = clock.now()
        _state.update {
            it.copy(
                entries = EntriesUIState(
                    items = loaded.entries.map { entry ->
                        entry.toCardUIModel(now, titleLanguage, cardLanguages)
                    }.toImmutableList(),
                    hasMore = loaded.hasMore,
                ),
            )
        }
    }

    /** The repository already cleared a rejected token, so the session flow turns this into a guest. */
    private fun onUnauthorized(error: Throwable): Boolean {
        if (error != DomainError.Unauthorized) return false
        sendEffect(
            ProfileEffect.ShowMessage(
                UiText.StringResource(StringResource.profile_session_expired),
            ),
        )
        return true
    }

    private fun sendEffect(effect: ProfileEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    /** Choosing a sort saves it (in its natural direction) and reloads the selected label. */
    private fun selectSort(sort: ListSort) {
        if (sort == listSort) return
        applySort(sort, reversed = false)
    }

    /** Flips the current sort's direction, saves it and reloads the selected label. */
    private fun toggleSortDirection() = applySort(listSort, reversed = !isSortReversed)

    private fun applySort(sort: ListSort, reversed: Boolean) {
        listSort = sort
        isSortReversed = reversed
        entriesByLabel.clear()
        renderSort()
        viewModelScope.launch {
            updateSettings {
                it.copy(
                    listSort = sort,
                    isListSortReversed = reversed,
                )
            }
        }
        selectedLabelId?.let { loadFirstPage(it, isRefresh = false) }
    }

    private fun renderSort() {
        _state.update {
            it.copy(
                sortOptions = listSort.toSortOptions(),
                // Title runs A to Z naturally; updates and votes run newest / highest first.
                isSortAscending = (listSort == ListSort.Title) != isSortReversed,
            )
        }
    }

    private data class LabelEntries(val entries: List<ListEntry>, val next: ListCursor?) {
        val hasMore: Boolean get() = next != null
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
