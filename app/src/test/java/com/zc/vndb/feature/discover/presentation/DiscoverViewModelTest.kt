package com.zc.vndb.feature.discover.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.util.FixedClock
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.discover.domain.model.DatabaseStats
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples
import com.zc.vndb.feature.discover.domain.repository.FakeDiscoverRepository
import com.zc.vndb.feature.discover.domain.usecase.GetDatabaseStatsUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetFeaturedNovelUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetQuoteOfTheDayUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetTopRatedNovelsUseCase
import com.zc.vndb.feature.discover.domain.usecase.GetTrendingReleasesUseCase
import com.zc.vndb.feature.discover.domain.usecase.RollRandomNovelUseCase
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class DiscoverViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeDiscoverRepository().apply {
        topRated = (1..20).map { DiscoverSamples.novel("v$it") }
        topRated.forEach { details[it.id] = DiscoverSamples.details(id = it.id) }
        releases = listOf(DiscoverSamples.release("r1", novelId = "v100"))
        stats = DatabaseStats(novelCount = 68053)
        latestNovelNumber = 10
        novelNumbers = listOf(10)
        quotes = listOf(DiscoverSamples.quote(id = "q1", novelId = "v11"))
    }

    private val settings = FakeSettingsRepository()

    private fun createViewModel() = DiscoverViewModel(
        getTopRatedNovels = GetTopRatedNovelsUseCase(repository),
        getFeaturedNovel = GetFeaturedNovelUseCase(
            repository,
            FixedClock(Instant.parse("2026-11-16T00:00:00Z")),
        ),
        getTrendingReleases = GetTrendingReleasesUseCase(repository),
        getDatabaseStats = GetDatabaseStatsUseCase(repository),
        getQuoteOfTheDay = GetQuoteOfTheDayUseCase(repository, Random(seed = 1)),
        rollRandomNovel = RollRandomNovelUseCase(repository, Random(seed = 1)),
        observeSettings = ObserveSettingsUseCase(settings),
    )

    @Test
    fun `loads every section on creation`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            assertTrue(awaitItem().isLoading)
            val loaded = awaitItem()
            assertFalse(loaded.isLoading)
            assertNull(loaded.error)
            assertNotNull(loaded.featured)
            assertEquals(10, loaded.topRated.size)
            assertEquals("v1", loaded.topRated.first().id)
            assertEquals(listOf("r1"), loaded.trending.map { it.releaseId })
            assertEquals(
                UiText.StringPlural(
                    PluralResource.discover_stats_novels,
                    68053,
                    listOf("%,d".format(68053)),
                ),
                loaded.statsLabel,
            )
        }
    }

    @Test
    fun `a top-rated failure is a screen error`() = runTest {
        repository.topRatedError = DomainError.Network
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(UiText.StringResource(StringResource.error_network), state.error?.message)
        assertNull(state.featured)
        assertTrue(state.topRated.isEmpty() && state.trending.isEmpty())
        assertNull(state.statsLabel)
    }

    @Test
    fun `failed hero, trending and stats only hide their sections`() = runTest {
        repository.details.clear()
        repository.releasesError = DomainError.Server
        repository.statsError = DomainError.Timeout
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.error)
        assertNull(state.featured)
        assertEquals(10, state.topRated.size)
        assertTrue(state.trending.isEmpty())
        assertNull(state.statsLabel)
    }

    @Test
    fun `retry reloads after a failure`() = runTest {
        repository.topRatedError = DomainError.Network
        val viewModel = createViewModel()
        advanceUntilIdle()

        repository.topRatedError = null
        viewModel.onIntent(DiscoverIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(10, viewModel.state.value.topRated.size)
    }

    @Test
    fun `random roll navigates to the rolled novel`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(DiscoverIntent.RandomRollClicked)
            assertEquals(DiscoverEffect.NavigateToNovel("v10"), awaitItem())
        }
        assertFalse(viewModel.state.value.isRolling)
    }

    @Test
    fun `a failed roll shows an error`() = runTest {
        repository.latestError = DomainError.RateLimited
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(DiscoverIntent.RandomRollClicked)
            val effect = assertIs<DiscoverEffect.ShowError>(awaitItem())
            assertEquals(
                UiText.StringResource(StringResource.error_rate_limited),
                effect.error.message,
            )
        }
        assertFalse(viewModel.state.value.isRolling)
    }

    @Test
    fun `clicks become navigation effects`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(DiscoverIntent.NovelClicked("v7"))
            assertEquals(DiscoverEffect.NavigateToNovel("v7"), awaitItem())
            viewModel.onIntent(DiscoverIntent.ReleaseClicked("r7"))
            assertEquals(DiscoverEffect.NavigateToRelease("r7"), awaitItem())
            viewModel.onIntent(DiscoverIntent.SeeAllTopRatedClicked)
            assertEquals(DiscoverEffect.NavigateToBrowse, awaitItem())
            viewModel.onIntent(DiscoverIntent.StatsClicked)
            assertEquals(DiscoverEffect.NavigateToBrowse, awaitItem())
        }
    }

    @Test
    fun `pull to refresh reloads every section while keeping them on screen`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.releases = listOf(DiscoverSamples.release("r2", novelId = "v100"))

        viewModel.state.test {
            skipItems(1)
            viewModel.onIntent(DiscoverIntent.Refresh)
            val refreshing = awaitItem()
            assertTrue(refreshing.isRefreshing)
            assertFalse(refreshing.isLoading)
            assertEquals(listOf("r1"), refreshing.trending.map { it.releaseId })

            val refreshed = awaitItem()
            assertFalse(refreshed.isRefreshing)
            assertEquals(listOf("r2"), refreshed.trending.map { it.releaseId })
        }
    }

    @Test
    fun `a failed refresh keeps the sections and shows an error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.topRatedError = DomainError.Network

        viewModel.effect.test {
            viewModel.onIntent(DiscoverIntent.Refresh)
            val effect = assertIs<DiscoverEffect.ShowError>(awaitItem())
            assertEquals(UiText.StringResource(StringResource.error_network), effect.error.message)
        }
        with(viewModel.state.value) {
            assertFalse(isRefreshing)
            assertNull(error)
            assertEquals(10, topRated.size)
        }
    }

    @Test
    fun `the quote of the day loads once with the other sections`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val quote = assertNotNull(viewModel.state.value.quote)
        assertEquals("People die when they are killed.", quote.text)
        assertEquals("v11", quote.novelId)
        assertEquals(1, repository.quoteCalls)
    }

    @Test
    fun `a failed quote only hides its section`() = runTest {
        repository.quoteError = DomainError.Server
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.error)
        assertNull(state.quote)
        assertEquals(10, state.topRated.size)
    }

    @Test
    fun `refreshing asks for a new quote`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.quotes = listOf(DiscoverSamples.quote(id = "q2", text = "Live happily!"))

        viewModel.onIntent(DiscoverIntent.Refresh)
        advanceUntilIdle()

        assertEquals("Live happily!", viewModel.state.value.quote?.text)
        assertEquals(2, repository.quoteCalls)
    }

    @Test
    fun `a refresh that cannot fetch a quote keeps the one on screen`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.quoteError = DomainError.Network

        viewModel.onIntent(DiscoverIntent.Refresh)
        advanceUntilIdle()

        assertEquals("People die when they are killed.", viewModel.state.value.quote?.text)
    }

    @Test
    fun `tapping the quote opens its novel`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val quote = assertNotNull(viewModel.state.value.quote)

        viewModel.effect.test {
            viewModel.onIntent(DiscoverIntent.NovelClicked(quote.novelId))
            assertEquals(DiscoverEffect.NavigateToNovel("v11"), awaitItem())
        }
    }

    @Test
    fun `new and trending uses the languages from settings`() = runTest {
        settings.current.value = AppSettings(trendingLanguages = listOf("ko", "zh-Hans"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf("ko", "zh-Hans"), repository.requestedReleaseLanguages)
        assertEquals(
            listOf("ko", "zh-Hans").toLanguagesLabel(),
            viewModel.state.value.trendingLanguagesLabel,
        )
    }
}
