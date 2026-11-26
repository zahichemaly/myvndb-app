package com.zc.vndb.feature.settings.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zc.vndb.BuildConfig
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.usecase.LogOutUseCase
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.usecase.RefreshLanguagesUseCase
import com.zc.vndb.common.domain.usecase.UpdateSettingsUseCase
import com.zc.vndb.common.ui.mapper.inPickerOrder
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.settings.domain.usecase.ClearImageCacheUseCase
import com.zc.vndb.feature.settings.domain.usecase.GetImageCacheSizeUseCase
import com.zc.vndb.feature.settings.presentation.home.mapper.toSizeLabel
import com.zc.vndb.feature.settings.presentation.home.mapper.withSettings
import com.zc.vndb.feature.settings.presentation.home.model.SettingsDialog
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

/** Every change is written at once; other screens pick it up from the shared settings. */
@KoinViewModel
class SettingsViewModel(
    observeSettings: ObserveSettingsUseCase,
    observeSession: ObserveSessionUseCase,
    observeLanguages: ObserveLanguagesUseCase,
    private val refreshLanguages: RefreshLanguagesUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val getImageCacheSize: GetImageCacheSizeUseCase,
    private val clearImageCache: ClearImageCacheUseCase,
    private val logOut: LogOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsState(
            versionLabel = UiText.StringResource(
                StringResource.settings_version,
                listOf(
                    UiText.StringResource(StringResource.app_name),
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                ),
            ),
        ),
    )

    val state: StateFlow<SettingsState> =
        combine(
            _state,
            observeSettings(),
            observeSession(),
            observeLanguages(),
        ) { state, settings, session, languages ->
            state.withSettings(settings, languages.inPickerOrder()).copy(
                isSignedIn = session != null,
                accountLabel = session?.let { UiText.StringValue(it.username) },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), _state.value)

    private val _effect = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect: Flow<SettingsEffect> = _effect.receiveAsFlow()

    init {
        measureImageCache()
    }

    @Suppress("CyclomaticComplexMethod") // A flat dispatch over the screen's intents.
    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.BackClicked -> sendEffect(SettingsEffect.NavigateBack)

            is SettingsIntent.DialogOpened -> openDialog(intent.dialog)

            SettingsIntent.DialogDismissed -> closeDialog()

            SettingsIntent.ThemeClicked -> sendEffect(SettingsEffect.OpenTheme)

            SettingsIntent.LicensesClicked -> sendEffect(SettingsEffect.OpenLicenses)

            is SettingsIntent.OptionSelected -> selectOption(intent.dialog, intent.key)

            is SettingsIntent.LanguagesConfirmed -> {
                closeDialog()
                if (intent.codes.isNotEmpty()) {
                    save {
                        it.copy(
                            trendingLanguages = intent.codes.toList(),
                        )
                    }
                }
            }

            is SettingsIntent.CardLanguagesConfirmed -> {
                closeDialog()
                save { it.copy(cardLanguages = intent.codes.toList()) }
            }

            is SettingsIntent.SpoilIfFinishedChanged -> save {
                it.copy(
                    spoilIfFinished = intent.isOn,
                )
            }

            // Turning sensitive content on asks for confirmation first; turning it off does not.
            is SettingsIntent.ShowSensitiveChanged -> if (intent.isOn) {
                _state.update { it.copy(dialog = SettingsDialog.ConfirmSensitive) }
            } else {
                save { it.copy(showSensitiveContent = false) }
            }

            SettingsIntent.SensitiveConfirmed -> {
                closeDialog()
                save { it.copy(showSensitiveContent = true) }
            }

            is SettingsIntent.IncludeAdultChanged -> save {
                it.copy(
                    includeAdultInBrowse = intent.isOn,
                )
            }

            SettingsIntent.ClearCacheConfirmed -> clearCache()

            SettingsIntent.LogOutConfirmed -> confirmLogOut()

            is SettingsIntent.LinkClicked -> sendEffect(SettingsEffect.OpenUrl(intent.url))
        }
    }

    private fun openDialog(dialog: SettingsDialog) {
        _state.update { it.copy(dialog = dialog) }
        // The language list refreshes in the background if it is stale; the dialog shows what it has.
        if (dialog == SettingsDialog.TrendingLanguages || dialog == SettingsDialog.CardLanguages) {
            viewModelScope.launch { refreshLanguages() }
        }
    }

    private fun selectOption(dialog: SettingsDialog, key: String) {
        closeDialog()
        when (dialog) {
            SettingsDialog.TitleLanguage -> TitleLanguage.entries.firstOrNull { it.name == key }
                ?.let { language -> save { it.copy(titleLanguage = language) } }

            SettingsDialog.SpoilerLevel -> SpoilerPreference.entries.firstOrNull { it.name == key }
                ?.let { level -> save { it.copy(defaultSpoilerLevel = level) } }

            else -> Unit
        }
    }

    private fun clearCache() {
        closeDialog()
        _state.update { it.copy(isClearingCache = true) }
        viewModelScope.launch {
            clearImageCache()
            _state.update { it.copy(isClearingCache = false) }
            sendEffect(
                SettingsEffect.ShowMessage(
                    UiText.StringResource(StringResource.settings_cache_cleared),
                ),
            )
            measureImageCache()
        }
    }

    /** Removes the token, then leaves Settings: Profile shows the log-in prompt again. */
    private fun confirmLogOut() {
        closeDialog()
        viewModelScope.launch {
            logOut()
            sendEffect(SettingsEffect.NavigateBack)
        }
    }

    private fun measureImageCache() {
        viewModelScope.launch {
            val size = getImageCacheSize()
            _state.update { it.copy(imageCacheLabel = size.toSizeLabel()) }
        }
    }

    private fun save(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { updateSettings(transform) }
    }

    private fun closeDialog() {
        _state.update { it.copy(dialog = null) }
    }

    private fun sendEffect(effect: SettingsEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    companion object {
        const val PRIVACY_URL = "https://vndb.org/d17#4"
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
