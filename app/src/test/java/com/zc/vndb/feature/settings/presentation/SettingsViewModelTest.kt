package com.zc.vndb.feature.settings.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.repository.ImageCacheRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.usecase.RefreshLanguagesUseCase
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.profile.domain.usecase.LogOutUseCase
import com.zc.vndb.feature.profile.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.feature.settings.domain.usecase.ClearImageCacheUseCase
import com.zc.vndb.feature.settings.domain.usecase.GetImageCacheSizeUseCase
import com.zc.vndb.feature.settings.domain.usecase.UpdateSettingsUseCase
import com.zc.vndb.feature.settings.presentation.model.SettingsDialog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settings = FakeSettingsRepository()
    private val sessions = FakeSessionRepository()

    private class FakeImageCache(var bytes: Long) : ImageCacheRepository {
        var cleared = false
        override suspend fun size() = bytes
        override suspend fun clear() {
            cleared = true
            bytes = 0
        }
    }

    private val cache = FakeImageCache(bytes = 149_000_000)
    private val languages = FakeLanguageRepository()

    private fun createViewModel() = SettingsViewModel(
        observeSettings = ObserveSettingsUseCase(settings),
        observeSession = ObserveSessionUseCase(sessions),
        observeLanguages = ObserveLanguagesUseCase(languages),
        refreshLanguages = RefreshLanguagesUseCase(languages),
        updateSettings = UpdateSettingsUseCase(settings),
        getImageCacheSize = GetImageCacheSizeUseCase(cache),
        clearImageCache = ClearImageCacheUseCase(cache),
        logOut = LogOutUseCase(sessions),
    )

    private fun TestScope.collect(state: StateFlow<SettingsState>) {
        backgroundScope.launch { state.collect {} }
    }

    @Test
    fun `shows the stored settings and the cache size`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertTrue(isLoaded)
            assertFalse(isSignedIn)
            assertEquals(
                UiText.StringResource(StringResource.settings_title_romanized),
                titleLanguageLabel,
            )
            assertEquals(UiText.StringValue("English, Japanese"), languagesLabel)
            assertEquals(
                UiText.StringResource(StringResource.settings_size_mb, listOf("142")),
                imageCacheLabel,
            )
        }
    }

    @Test
    fun `choices are saved and close the dialog`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)

        viewModel.onIntent(SettingsIntent.DialogOpened(SettingsDialog.TitleLanguage))
        viewModel.onIntent(
            SettingsIntent.OptionSelected(
                SettingsDialog.TitleLanguage,
                TitleLanguage.Original.name,
            ),
        )
        viewModel.onIntent(
            SettingsIntent.OptionSelected(
                SettingsDialog.SpoilerLevel,
                SpoilerPreference.Major.name,
            ),
        )
        viewModel.onIntent(SettingsIntent.LanguagesConfirmed(setOf("ko")))
        viewModel.onIntent(SettingsIntent.IncludeAdultChanged(true))
        advanceUntilIdle()

        with(settings.current.value) {
            assertEquals(TitleLanguage.Original, titleLanguage)
            assertEquals(SpoilerPreference.Major, defaultSpoilerLevel)
            assertEquals(listOf("ko"), trendingLanguages)
            assertTrue(includeAdultInBrowse)
        }
        assertNull(viewModel.state.value.dialog)
    }

    @Test
    fun `sensitive content needs confirmation to turn on but not off`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)

        viewModel.onIntent(SettingsIntent.ShowSensitiveChanged(true))
        advanceUntilIdle()
        assertEquals(SettingsDialog.ConfirmSensitive, viewModel.state.value.dialog)
        assertFalse(settings.current.value.showSensitiveContent)

        viewModel.onIntent(SettingsIntent.SensitiveConfirmed)
        advanceUntilIdle()
        assertTrue(settings.current.value.showSensitiveContent)

        viewModel.onIntent(SettingsIntent.ShowSensitiveChanged(false))
        advanceUntilIdle()
        assertFalse(settings.current.value.showSensitiveContent)
        assertNull(viewModel.state.value.dialog)
    }

    @Test
    fun `clearing the cache empties it and says so`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.ClearCacheConfirmed)
            assertEquals(
                SettingsEffect.ShowMessage(
                    UiText.StringResource(StringResource.settings_cache_cleared),
                ),
                awaitItem(),
            )
        }
        advanceUntilIdle()
        assertTrue(cache.cleared)
        assertEquals(
            UiText.StringResource(StringResource.settings_size_bytes, listOf("0")),
            viewModel.state.value.imageCacheLabel,
        )
    }

    @Test
    fun `signed-in users can use spoil me if finished, links open`() = runTest {
        sessions.current.value = Session("u2", "Yorhel", canReadPrivateList = true)
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSignedIn)

        viewModel.onIntent(SettingsIntent.SpoilIfFinishedChanged(true))
        advanceUntilIdle()
        assertTrue(settings.current.value.spoilIfFinished)

        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.LinkClicked(SettingsViewModel.PRIVACY_URL))
            assertEquals(SettingsEffect.OpenUrl("https://vndb.org/d17#4"), awaitItem())
        }
    }

    @Test
    fun `signed-in users see their account and log out after confirming, which leaves Settings`() =
        runTest {
            sessions.current.value = Session("u2", "Yorhel", canReadPrivateList = true)
            val viewModel = createViewModel()
            collect(viewModel.state)
            advanceUntilIdle()
            assertEquals(
                UiText.StringValue("Yorhel"),
                viewModel.state.value.accountLabel,
            )

            viewModel.onIntent(SettingsIntent.DialogOpened(SettingsDialog.ConfirmLogOut))
            advanceUntilIdle()
            assertEquals(SettingsDialog.ConfirmLogOut, viewModel.state.value.dialog)

            viewModel.effect.test {
                viewModel.onIntent(SettingsIntent.LogOutConfirmed)
                assertEquals(SettingsEffect.NavigateBack, awaitItem())
            }
            advanceUntilIdle()
            assertNull(sessions.current.value)
            assertNull(viewModel.state.value.accountLabel)
            assertNull(viewModel.state.value.dialog)
        }

    @Test
    fun `guests have no account section`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        assertNull(viewModel.state.value.accountLabel)
    }

    @Test
    fun `the Theme row shows the current theme and opens the Theme screen`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(StringResource.theme_original),
            viewModel.state.value.themeLabel,
        )
        viewModel.effect.test {
            viewModel.onIntent(SettingsIntent.ThemeClicked)
            assertEquals(SettingsEffect.OpenTheme, awaitItem())
        }
    }

    @Test
    fun `the language dialog lists the fetched languages, common ones first, with the saved ones ticked`() =
        runTest {
            languages.current.value = listOf(
                LanguageOption("ar", "Arabic"),
                LanguageOption("ja", "Japanese"),
                LanguageOption("en", "English"),
                LanguageOption("eu", "Basque"),
            )
            val viewModel = createViewModel()
            collect(viewModel.state)
            advanceUntilIdle()

            val options = viewModel.state.value.languageOptions
            assertEquals(listOf("en", "ja", "ar", "eu"), options.map { it.key })
            assertEquals(listOf(true, true, false, false), options.map { it.isSelected })
            assertEquals(UiText.StringValue("English"), options.first().label)
        }

    @Test
    fun `a saved language that is not in the list can still be unticked`() = runTest {
        settings.current.value = AppSettings(trendingLanguages = listOf("en", "xx"))
        languages.current.value = listOf(LanguageOption("en", "English"))
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        val options = viewModel.state.value.languageOptions
        assertEquals(listOf("en", "xx"), options.map { it.key })
        assertTrue(options.all { it.isSelected })
    }

    @Test
    fun `opening the language dialog refreshes the list, other dialogs do not`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        assertEquals(0, languages.refreshCalls)

        viewModel.onIntent(SettingsIntent.DialogOpened(SettingsDialog.TitleLanguage))
        advanceUntilIdle()
        assertEquals(0, languages.refreshCalls)

        viewModel.onIntent(SettingsIntent.DialogOpened(SettingsDialog.TrendingLanguages))
        advanceUntilIdle()
        assertEquals(1, languages.refreshCalls)
        assertEquals(SettingsDialog.TrendingLanguages, viewModel.state.value.dialog)
    }

    @Test
    fun `a failed refresh still opens the language dialog with what it has`() = runTest {
        languages.refreshError = DomainError.Network
        val viewModel = createViewModel()
        collect(viewModel.state)

        viewModel.onIntent(SettingsIntent.DialogOpened(SettingsDialog.TrendingLanguages))
        advanceUntilIdle()

        assertEquals(SettingsDialog.TrendingLanguages, viewModel.state.value.dialog)
        assertTrue(viewModel.state.value.languageOptions.isNotEmpty())
    }
}
