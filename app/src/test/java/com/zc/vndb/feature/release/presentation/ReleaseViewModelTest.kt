package com.zc.vndb.feature.release.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppConfig
import com.zc.vndb.common.domain.model.AppConfigSamples
import com.zc.vndb.common.domain.model.ExternalLink
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.GetAppConfigUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.release.domain.model.ReleaseSamples
import com.zc.vndb.feature.release.domain.repository.FakeReleaseRepository
import com.zc.vndb.feature.release.domain.usecase.GetReleaseUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class ReleaseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeReleaseRepository().apply {
        releases["r7668"] =
            ReleaseSamples.release()
    }

    private val settings = FakeSettingsRepository()

    private fun createViewModel(appConfig: AppConfig = AppConfigSamples.development) =
        ReleaseViewModel(
            releaseId = "r7668",
            getRelease = GetReleaseUseCase(repository),
            observeSettings = ObserveSettingsUseCase(settings),
            getAppConfig = GetAppConfigUseCase(appConfig),
        )

    @Test
    fun `loads every section`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertEquals(UiText.StringValue("Steins;Gate"), hero?.title)
            assertEquals("https://vndb.org/r7668", links.first().url)
            assertEquals(listOf("v2002"), novels.map { it.id })
            assertEquals(1, languages.size)
            assertEquals(1, producers.size)
            assertNotNull(notes)
        }
    }

    @Test
    fun `a missing release is an error and retry reloads`() = runTest {
        val saved = repository.releases.remove("r7668")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(
            UiText.StringResource(StringResource.error_not_found),
            viewModel.state.value.error?.message,
        )

        repository.releases["r7668"] = checkNotNull(saved)
        viewModel.onIntent(ReleaseIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
        assertNotNull(viewModel.state.value.hero)
    }

    @Test
    fun `notes toggle and actions become effects`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ReleaseIntent.NotesToggled)
        assertTrue(viewModel.state.value.isNotesExpanded)

        viewModel.effect.test {
            viewModel.onIntent(ReleaseIntent.BackClicked)
            assertEquals(ReleaseEffect.NavigateBack, awaitItem())
            viewModel.onIntent(ReleaseIntent.ShareClicked)
            assertEquals(ReleaseEffect.Share("Steins;Gate", "https://vndb.org/r7668"), awaitItem())
            viewModel.onIntent(ReleaseIntent.OpenInBrowserClicked)
            assertEquals(ReleaseEffect.OpenUrl("https://vndb.org/r7668"), awaitItem())
            viewModel.onIntent(ReleaseIntent.LinkClicked("http://steinsgate.jp/"))
            assertEquals(ReleaseEffect.OpenUrl("http://steinsgate.jp/"), awaitItem())
            viewModel.onIntent(ReleaseIntent.NovelClicked("v2002"))
            assertEquals(ReleaseEffect.NavigateToNovel("v2002"), awaitItem())
        }
    }

    @Test
    fun `refresh reloads behind the pull indicator without a loading state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            skipItems(1)
            viewModel.onIntent(ReleaseIntent.Refresh)
            val refreshing = awaitItem()
            assertTrue(refreshing.isRefreshing)
            assertFalse(refreshing.isLoading)
            assertNotNull(refreshing.hero)

            cancelAndConsumeRemainingEvents()
        }
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isRefreshing)
        assertNotNull(viewModel.state.value.hero)
    }

    @Test
    fun `a failed refresh keeps the release and shows an error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.releases.remove("r7668")

        viewModel.effect.test {
            viewModel.onIntent(ReleaseIntent.Refresh)
            advanceUntilIdle()
            assertEquals(
                UiText.StringResource(StringResource.error_not_found),
                (awaitItem() as ReleaseEffect.ShowError).error.message,
            )
        }
        with(viewModel.state.value) {
            assertFalse(isRefreshing)
            assertNull(error)
            assertNotNull(hero)
        }
    }

    @Test
    fun `a production build shows only vndb org for an adult release`() = runTest {
        repository.releases["r7668"] = ReleaseSamples.release(
            links = listOf(ExternalLink("Getchu", "https://www.getchu.com/")),
        ).copy(minAge = 18)
        val viewModel = createViewModel(AppConfigSamples.production)
        advanceUntilIdle()

        assertEquals(listOf("https://vndb.org/r7668"), viewModel.state.value.links.map { it.url })
    }
}
