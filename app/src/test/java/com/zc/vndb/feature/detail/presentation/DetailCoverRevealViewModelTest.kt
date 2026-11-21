package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.detail.domain.model.DetailSamples
import com.zc.vndb.feature.detail.domain.repository.FakeDetailRepository
import com.zc.vndb.feature.detail.domain.usecase.GetNovelCastUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelCoversUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelDetailUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelEditionsUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelQuotesUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelReleasesUseCase
import com.zc.vndb.feature.detail.domain.usecase.GetNovelScreenshotsUseCase
import com.zc.vndb.feature.detail.presentation.model.CoverRevealRequest
import com.zc.vndb.feature.detail.presentation.model.DetailSection
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import com.zc.vndb.feature.userlist.domain.usecase.GetMyListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.IsNovelFinishedUseCase
import com.zc.vndb.feature.userlist.domain.usecase.ObserveListChangesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

/** Tapping a blurred cover asks "I'm 18 or older" first; confirming shows this page's covers. */
class DetailCoverRevealViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeDetailRepository().apply {
        novels["v2002"] = DetailSamples.novel()
        covers = listOf(DetailSamples.coverArt("front"), DetailSamples.coverArt("back", "r2"))
    }
    private val settings = FakeSettingsRepository()
    private val languages = FakeLanguageRepository()
    private val sessions = FakeSessionRepository()
    private val userListRepository = FakeUserListRepository()

    private fun createViewModel() = DetailViewModel(
        novelId = "v2002",
        getNovelDetail = GetNovelDetailUseCase(repository),
        getNovelEditions = GetNovelEditionsUseCase(repository),
        getNovelCast = GetNovelCastUseCase(repository),
        getNovelScreenshots = GetNovelScreenshotsUseCase(repository),
        getNovelCovers = GetNovelCoversUseCase(repository),
        getNovelReleases = GetNovelReleasesUseCase(repository),
        getNovelQuotes = GetNovelQuotesUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
        observeLanguages = ObserveLanguagesUseCase(languages),
        isNovelFinished = IsNovelFinishedUseCase(sessions, userListRepository),
        observeSession = ObserveSessionUseCase(sessions),
        getMyListEntry = GetMyListEntryUseCase(sessions, userListRepository),
        observeListChanges = ObserveListChangesUseCase(userListRepository),
    )

    @Test
    fun `covers are not revealed and nothing is asked until a blurred cover is tapped`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.areCoversRevealed)
        assertNull(viewModel.state.value.coverReveal)
    }

    @Test
    fun `tapping the page's cover asks the question without showing anything yet`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(DetailIntent.CoverRevealRequested(viewerIndex = null))

        assertEquals(CoverRevealRequest(viewerIndex = null), viewModel.state.value.coverReveal)
        assertFalse(viewModel.state.value.areCoversRevealed)
    }

    @Test
    fun `confirming 18 or older reveals the page's covers and closes the question`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.CoverRevealRequested(viewerIndex = null))

        viewModel.onIntent(DetailIntent.CoverRevealConfirmed)

        assertTrue(viewModel.state.value.areCoversRevealed)
        assertNull(viewModel.state.value.coverReveal)
        assertNull(viewModel.state.value.viewer)
    }

    @Test
    fun `cancelling leaves the covers blurred`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.CoverRevealRequested(viewerIndex = 1))

        viewModel.onIntent(DetailIntent.CoverRevealDismissed)

        assertNull(viewModel.state.value.coverReveal)
        assertFalse(viewModel.state.value.areCoversRevealed)
        assertNull(viewModel.state.value.viewer)
    }

    @Test
    fun `a carousel cover opens the viewer only after the question is confirmed`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.SectionNeeded(DetailSection.Covers))
        advanceUntilIdle()

        viewModel.onIntent(DetailIntent.CoverRevealRequested(viewerIndex = 1))
        assertNull(viewModel.state.value.viewer)

        viewModel.onIntent(DetailIntent.CoverRevealConfirmed)

        val viewer = assertNotNull(viewModel.state.value.viewer)
        assertEquals(1, viewer.startIndex)
        assertEquals(listOf("front", "back"), viewer.images.map { it.url })
        assertTrue(viewModel.state.value.areCoversRevealed)
    }

    @Test
    fun `a cover that needs no question still opens the viewer straight away`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.SectionNeeded(DetailSection.Covers))
        advanceUntilIdle()

        viewModel.onIntent(DetailIntent.CoverClicked(0))

        assertEquals(0, viewModel.state.value.viewer?.startIndex)
        assertNull(viewModel.state.value.coverReveal)
    }

    @Test
    fun `once revealed, closing the viewer keeps the covers revealed`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.SectionNeeded(DetailSection.Covers))
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.CoverRevealRequested(viewerIndex = 0))
        viewModel.onIntent(DetailIntent.CoverRevealConfirmed)

        viewModel.onIntent(DetailIntent.ViewerDismissed)

        assertNull(viewModel.state.value.viewer)
        assertTrue(viewModel.state.value.areCoversRevealed)
    }
}
