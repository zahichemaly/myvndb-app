package com.zc.vndb.feature.character.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.error.mapper.toErrorUIModel
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toSpoilerLevel
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.mapper.bbCodeToPlainText
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.character.domain.model.CharacterProfile
import com.zc.vndb.feature.character.domain.usecase.GetCharacterUseCase
import com.zc.vndb.feature.character.presentation.component.appearancerow.mapper.toRowUIModel
import com.zc.vndb.feature.character.presentation.component.characterhero.mapper.toHeroUIModel
import com.zc.vndb.feature.character.presentation.component.traitgroups.mapper.toHiddenTraitsLabel
import com.zc.vndb.feature.character.presentation.component.traitgroups.mapper.toTraitGroups
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
class CharacterViewModel(
    @InjectedParam private val characterId: String,
    private val getCharacter: GetCharacterUseCase,
    private val observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterState(characterId = characterId))
    val state: StateFlow<CharacterState> = _state.asStateFlow()

    private val _effect = Channel<CharacterEffect>(Channel.BUFFERED)
    val effect: Flow<CharacterEffect> = _effect.receiveAsFlow()

    /** Kept to re-map when the spoiler level changes. */
    private var profile: CharacterProfile? = null
    private var loadJob: Job? = null
    private var titleLanguage = TitleLanguage.Romanized

    /** Settings → Show sensitive content (always off in production): lists sexual traits. */
    private var showSensitiveContent = false

    /** The starting spoiler level (Settings) is applied on the first successful load only. */
    private var isStartApplied = false

    init {
        onIntent(CharacterIntent.Load)
    }

    fun onIntent(intent: CharacterIntent) {
        when (intent) {
            CharacterIntent.Load,
            CharacterIntent.Retry,
            -> load()

            CharacterIntent.Refresh -> refresh()

            CharacterIntent.BackClicked -> sendEffect(CharacterEffect.NavigateBack)

            CharacterIntent.ShareClicked -> sendEffect(
                CharacterEffect.Share(profile?.name.orEmpty(), vndbUrl()),
            )

            CharacterIntent.OpenInBrowserClicked -> sendEffect(CharacterEffect.OpenUrl(vndbUrl()))

            is CharacterIntent.SpoilerLevelSelected -> {
                _state.update { it.copy(spoilerLevel = intent.level) }
                render()
            }

            CharacterIntent.DescriptionToggled ->
                _state.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }

            is CharacterIntent.NovelClicked -> sendEffect(
                CharacterEffect.NavigateToNovel(intent.id),
            )

            is CharacterIntent.StaffClicked -> sendEffect(
                CharacterEffect.NavigateToStaff(intent.id),
            )
        }
    }

    private fun load() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, isRefreshing = false, error = null) }
        loadJob = viewModelScope.launch {
            val settings = observeSettings.current()
            titleLanguage = settings.titleLanguage
            showSensitiveContent = settings.showSensitiveContent
            getCharacter(characterId)
                .onSuccess { loaded ->
                    profile = loaded
                    if (!isStartApplied) {
                        isStartApplied = true
                        _state.update {
                            it.copy(
                                spoilerLevel = settings.defaultSpoilerLevel.toSpoilerLevel(),
                            )
                        }
                    }
                    showProfile(loaded)
                    _state.update { it.copy(isLoading = false) }
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
     * Reloads the character behind the pull-to-refresh indicator. With nothing loaded yet (first
     * load in flight or failed) it is a plain load; a failed refresh keeps what is shown.
     */
    private fun refresh() {
        val current = _state.value
        if (current.isRefreshing || current.isLoading) return
        if (current.error != null) return load()
        loadJob?.cancel()
        _state.update { it.copy(isRefreshing = true) }
        loadJob = viewModelScope.launch {
            val settings = observeSettings.current()
            titleLanguage = settings.titleLanguage
            showSensitiveContent = settings.showSensitiveContent
            getCharacter(characterId)
                .onSuccess { loaded ->
                    showProfile(loaded)
                    _state.update { it.copy(isRefreshing = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isRefreshing = false) }
                    sendEffect(CharacterEffect.ShowError(error.toErrorUIModel()))
                }
        }
    }

    private fun showProfile(loaded: CharacterProfile) {
        profile = loaded
        _state.update {
            it.copy(aliases = loaded.aliases.map(UiText::StringValue).toImmutableList())
        }
        render()
    }

    /** Everything that depends on the spoiler level. */
    private fun render() {
        val current = profile ?: return
        val level = _state.value.spoilerLevel
        val visible = current.appearances.filter { it.spoilerLevel <= level.maxLevel }
        val hiddenAppearances = current.appearances.size - visible.size
        _state.update {
            it.copy(
                hero = current.toHeroUIModel(level),
                description = current.description
                    ?.bbCodeToPlainText(
                        keepParagraphs = true,
                        showSpoilers = level != SpoilerLevel.None,
                    )
                    ?.takeIf { text -> text.isNotEmpty() }
                    ?.let(UiText::StringValue),
                traitGroups = current.traits.toTraitGroups(level, showSensitiveContent),
                hiddenTraitsLabel = current.traits.toHiddenTraitsLabel(level, showSensitiveContent),
                appearances = visible.map { appearance ->
                    appearance.toRowUIModel(titleLanguage)
                }.toImmutableList(),
                hiddenAppearancesLabel = hiddenAppearances.takeIf { count -> count > 0 }
                    ?.let { count ->
                        UiText.StringPlural(
                            PluralResource.detail_hidden_by_spoiler,
                            count,
                            listOf(count),
                        )
                    },
            )
        }
    }

    private fun vndbUrl(): String = "https://vndb.org/$characterId"

    private fun sendEffect(effect: CharacterEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
