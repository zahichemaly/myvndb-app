package com.zc.vndb.feature.discover.presentation.trending

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples
import com.zc.vndb.feature.discover.domain.model.ReleasesPage
import com.zc.vndb.feature.discover.domain.repository.FakeDiscoverRepository
import com.zc.vndb.feature.discover.domain.usecase.GetTrendingReleasesPageUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class TrendingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeDiscoverRepository()
    private val settings = FakeSettingsRepository()

    private fun createViewModel() = TrendingViewModel(
        getTrendingPage = GetTrendingReleasesPageUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
    )

    private fun page(vararg releases: Pair<String, String>, hasMore: Boolean) = ReleasesPage(
        releases = releases.map { (id, novelId) -> DiscoverSamples.release(id, novelId = novelId) },
        hasMore = hasMore,
    )

    private fun TrendingViewModel.rowIds() = state.value.rows.map { it.releaseId }

    @Test
    fun `loads the first page with the languages it covers`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", "r2" to "v2", hasMore = false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf("r1", "r2"), viewModel.rowIds())
        assertEquals(AppSettings().trendingLanguages.toLanguagesLabel(), state.languagesLabel)
        assertEquals(listOf(1), repository.requestedReleasePages)
    }

    @Test
    fun `scrolling to the end appends the next page without repeating a novel`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", hasMore = true)
        repository.releasePages[2] = page("r2" to "v1", "r3" to "v2", hasMore = false)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TrendingIntent.EndReached)
        advanceUntilIdle()
        viewModel.onIntent(TrendingIntent.EndReached)
        advanceUntilIdle()

        assertEquals(listOf("r1", "r3"), viewModel.rowIds())
        assertEquals(listOf(1, 2), repository.requestedReleasePages)
    }

    @Test
    fun `a page that brings nothing new is skipped`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", hasMore = true)
        repository.releasePages[2] = page("r2" to "v1", hasMore = true)
        repository.releasePages[3] = page("r3" to "v3", hasMore = false)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TrendingIntent.EndReached)
        advanceUntilIdle()

        assertEquals(listOf("r1", "r3"), viewModel.rowIds())
        assertEquals(listOf(1, 2, 3), repository.requestedReleasePages)
    }

    @Test
    fun `a failed next page waits for Retry instead of loading on scroll`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", hasMore = true)
        repository.releasePages[2] = page("r2" to "v2", hasMore = false)
        repository.failingReleasePages += 2
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TrendingIntent.EndReached)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.loadMoreFailed)
        viewModel.onIntent(TrendingIntent.EndReached)
        advanceUntilIdle()
        assertEquals(listOf(1, 2), repository.requestedReleasePages)

        repository.failingReleasePages.clear()
        viewModel.onIntent(TrendingIntent.LoadMoreRetried)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.loadMoreFailed)
        assertEquals(listOf("r1", "r2"), viewModel.rowIds())
    }

    @Test
    fun `a failed first page is an error that Retry clears`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", hasMore = false)
        repository.failingReleasePages += 1
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)

        repository.failingReleasePages.clear()
        viewModel.onIntent(TrendingIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(listOf("r1"), viewModel.rowIds())
    }

    @Test
    fun `a failed refresh keeps the rows and reports the error`() = runTest {
        repository.releasePages[1] = page("r1" to "v1", hasMore = false)
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            repository.failingReleasePages += 1
            viewModel.onIntent(TrendingIntent.Refresh)
            advanceUntilIdle()

            assertIs<TrendingEffect.ShowError>(awaitItem())
        }
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals(listOf("r1"), viewModel.rowIds())
    }

    @Test
    fun `nothing in the user's languages is the empty state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `rows and back navigate`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(TrendingIntent.NovelClicked("v1"))
            assertEquals(TrendingEffect.NavigateToNovel("v1"), awaitItem())
            viewModel.onIntent(TrendingIntent.ReleaseClicked("r1"))
            assertEquals(TrendingEffect.NavigateToRelease("r1"), awaitItem())
            viewModel.onIntent(TrendingIntent.BackClicked)
            assertEquals(TrendingEffect.NavigateBack, awaitItem())
        }
    }
}
