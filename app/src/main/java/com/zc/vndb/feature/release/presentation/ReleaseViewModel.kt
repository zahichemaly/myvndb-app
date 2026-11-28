package com.zc.vndb.feature.release.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.GetAppConfigUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.feature.release.domain.model.ReleaseDetail
import com.zc.vndb.feature.release.domain.usecase.GetReleaseUseCase
import com.zc.vndb.feature.release.presentation.component.releasehero.mapper.toHeroUIModel
import com.zc.vndb.feature.release.presentation.component.releasenovelrow.mapper.toRowUIModel
import com.zc.vndb.feature.release.presentation.mapper.releaseUrl
import com.zc.vndb.feature.release.presentation.mapper.toImageUIModels
import com.zc.vndb.feature.release.presentation.mapper.toLanguageUIModels
import com.zc.vndb.feature.release.presentation.mapper.toLinks
import com.zc.vndb.feature.release.presentation.mapper.toNotes
import com.zc.vndb.feature.release.presentation.mapper.toProducerUIModels
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
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ReleaseViewModel(
    @InjectedParam private val releaseId: String,
    private val getRelease: GetReleaseUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    getAppConfig: GetAppConfigUseCase,
) : ViewModel() {

    /** False in builds that never show sensitive content: adult releases keep only the vndb.org link. */
    private val showsSensitiveContent = getAppConfig().showSensitiveContent

    private val _state = MutableStateFlow(ReleaseState(releaseId = releaseId))
    val state: StateFlow<ReleaseState> = _state.asStateFlow()

    private val _effect = Channel<ReleaseEffect>(Channel.BUFFERED)
    val effect: Flow<ReleaseEffect> = _effect.receiveAsFlow()

    private var title: String = ""
    private var loadJob: Job? = null

    init {
        onIntent(ReleaseIntent.Load)
    }

    fun onIntent(intent: ReleaseIntent) {
        when (intent) {
            ReleaseIntent.Load,
            ReleaseIntent.Retry,
            -> load()

            ReleaseIntent.Refresh -> refresh()

            ReleaseIntent.BackClicked -> sendEffect(ReleaseEffect.NavigateBack)

            ReleaseIntent.ShareClicked -> sendEffect(
                ReleaseEffect.Share(title, releaseUrl(releaseId)),
            )

            ReleaseIntent.OpenInBrowserClicked -> sendEffect(
                ReleaseEffect.OpenUrl(releaseUrl(releaseId)),
            )

            is ReleaseIntent.LinkClicked -> sendEffect(ReleaseEffect.OpenUrl(intent.url))

            is ReleaseIntent.NovelClicked -> sendEffect(ReleaseEffect.NavigateToNovel(intent.id))

            is ReleaseIntent.ProducerClicked -> sendEffect(
                ReleaseEffect.NavigateToProducer(intent.id),
            )

            ReleaseIntent.NotesToggled -> _state.update {
                it.copy(
                    isNotesExpanded = !it.isNotesExpanded,
                )
            }
        }
    }

    private fun load() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, isRefreshing = false, error = null) }
        loadJob = viewModelScope.launch {
            val titleLanguage = observeSettings.current().titleLanguage
            getRelease(releaseId)
                .onSuccess { release ->
                    _state.update {
                        release.toLoadedState(it, titleLanguage).copy(isLoading = false)
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
     * Reloads the release behind the pull-to-refresh indicator. With nothing loaded yet (first
     * load in flight or failed) it is a plain load; a failed refresh keeps what is shown.
     */
    private fun refresh() {
        val current = _state.value
        if (current.isRefreshing || current.isLoading) return
        if (current.error != null) return load()
        loadJob?.cancel()
        _state.update { it.copy(isRefreshing = true) }
        loadJob = viewModelScope.launch {
            val titleLanguage = observeSettings.current().titleLanguage
            getRelease(releaseId)
                .onSuccess { release ->
                    _state.update {
                        release.toLoadedState(it, titleLanguage).copy(isRefreshing = false)
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isRefreshing = false) }
                    sendEffect(ReleaseEffect.ShowError(error.toErrorUIModel()))
                }
        }
    }

    private fun ReleaseDetail.toLoadedState(
        current: ReleaseState,
        titleLanguage: TitleLanguage,
    ): ReleaseState {
        this@ReleaseViewModel.title = title
        return current.copy(
            hero = toHeroUIModel(),
            links = toLinks(showsSensitiveContent),
            novels = novels.map { novel -> novel.toRowUIModel(titleLanguage) }.toImmutableList(),
            languages = toLanguageUIModels(),
            producers = toProducerUIModels(),
            images = toImageUIModels(),
            notes = toNotes(),
        )
    }

    private fun sendEffect(effect: ReleaseEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
