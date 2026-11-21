package com.zc.vndb.feature.detail.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.model.SessionSamples
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.domain.model.VoiceActor
import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
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
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.DetailSection
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
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

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeDetailRepository().apply {
        novels["v2002"] = DetailSamples.novel(
            description = "Intro. [spoiler]Twist[/spoiler]",
            tags = listOf(DetailSamples.tag("Visible"), DetailSamples.tag("Hidden", spoiler = 2)),
            relations = listOf(DetailSamples.relation("v17102")),
        )
        credits = DetailSamples.credits(
            voiceActors = mapOf("c1" to VoiceActor("s7", "Miyano Mamoru")),
        )
        characters = listOf(
            DetailSamples.character("c1", CharacterRole.Main),
            DetailSamples.character("c2", CharacterRole.Primary, spoiler = 1),
        )
        editions = listOf(DetailSamples.edition("r1", minAge = 18))
        screenshots = listOf(
            DetailSamples.screenshot("safe"),
            DetailSamples.screenshot("explicit", sexual = 2.0),
        )
        releases = (1..60).map { DetailSamples.releaseSummary("r$it") }
        quotes = (1..30).map { DetailSamples.quote("q$it") }
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

    private fun DetailViewModel.need(section: DetailSection) = onIntent(
        DetailIntent.SectionNeeded(section),
    )

    @Test
    fun `opening the page only loads the overview`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf("novel"), repository.calls)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(UiText.StringValue("STEINS;GATE"), state.hero?.title)
        assertNull(state.hero?.ageLabel)
        assertNotNull(state.hero?.scoreLabel)
        assertEquals("https://vndb.org/v2002", state.links.first().url)
        assertEquals(listOf("v17102"), state.relations.map { it.id })
        assertEquals(SectionState.Idle, state.editions)
        assertEquals(SectionState.Idle, state.cast)
    }

    @Test
    fun `a section loads once, when first needed, and editions bring the age chip`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.need(DetailSection.Editions)
        viewModel.need(DetailSection.Editions)
        advanceUntilIdle()
        viewModel.need(DetailSection.Editions)
        advanceUntilIdle()

        assertEquals(listOf("novel", "editions"), repository.calls)
        val state = viewModel.state.value
        assertEquals(listOf("r1-win"), state.editions.dataOrNull?.map { it.key })
        assertEquals(
            UiText.StringResource(StringResource.detail_age_min, listOf(18)),
            state.hero?.ageLabel,
        )
    }

    @Test
    fun `cast loads with voice actors and follows the spoiler filter`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.need(DetailSection.Cast)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertEquals(UiText.StringValue("Intro."), synopsis)
            val cast = checkNotNull(this.cast.dataOrNull)
            assertEquals(listOf("c1"), cast.preview.map { it.id })
            assertEquals(
                UiText.StringResource(StringResource.detail_cv, listOf("Miyano Mamoru")),
                cast.preview.first().voiceActorLabel,
            )
            assertEquals("s7", cast.preview.first().voiceActorId)
            assertNotNull(cast.hiddenLabel)
            assertNotNull(hiddenTagsLabel)
            assertEquals(
                UiText.StringResource(
                    StringResource.detail_tab_with_count,
                    listOf(UiText.StringResource(StringResource.detail_tab_cast), 2),
                ),
                tabs.single { it.tab == DetailTab.Cast }.label,
            )
        }

        viewModel.onIntent(DetailIntent.SpoilerLevelSelected(SpoilerLevel.Major))

        with(viewModel.state.value) {
            assertEquals(SpoilerLevel.Major, spoilerLevel)
            assertEquals(UiText.StringValue("Intro. Twist"), synopsis)
            assertEquals(listOf("c1", "c2"), cast.dataOrNull?.preview?.map { it.id })
            assertNull(cast.dataOrNull?.hiddenLabel)
            assertNull(hiddenTagsLabel)
        }
    }

    @Test
    fun `a failed section keeps the page and retries on its own`() = runTest {
        repository.screenshotsError = DomainError.Server
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.need(DetailSection.Screenshots)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertTrue(viewModel.state.value.screenshots is SectionState.Failed)

        repository.screenshotsError = null
        viewModel.onIntent(DetailIntent.SectionRetried(DetailSection.Screenshots))
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.screenshots.dataOrNull?.visibleCount)
    }

    @Test
    fun `content toggles re-filter loaded screenshots and the viewer`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.need(DetailSection.Screenshots)
        advanceUntilIdle()

        viewModel.onIntent(DetailIntent.ContentFilterToggled(ContentFilter.Explicit))
        assertEquals(2, viewModel.state.value.screenshots.dataOrNull?.visibleCount)
        assertEquals(listOf("novel", "screenshots"), repository.calls)

        viewModel.onIntent(DetailIntent.ScreenshotClicked(1))
        assertEquals("explicit", viewModel.state.value.viewer?.images?.get(1)?.url)
        viewModel.onIntent(DetailIntent.ViewerDismissed)
        assertNull(viewModel.state.value.viewer)
    }

    @Test
    fun `releases and quotes page on load more`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.need(DetailSection.Releases)
        viewModel.need(DetailSection.Quotes)
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.releases.dataOrNull?.hasMore)
        assertEquals(25, viewModel.state.value.quotes.dataOrNull?.items?.size)

        viewModel.onIntent(DetailIntent.LoadMoreClicked(DetailSection.Releases))
        viewModel.onIntent(DetailIntent.LoadMoreClicked(DetailSection.Quotes))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(60, state.releases.dataOrNull?.items?.sumOf { it.releases.size })
        assertEquals(false, state.releases.dataOrNull?.hasMore)
        assertEquals(30, state.quotes.dataOrNull?.items?.size)
        assertEquals(
            listOf("novel", "releases:1", "quotes:1", "releases:2", "quotes:2"),
            repository.calls,
        )
    }

    @Test
    fun `a failed next page keeps the loaded quotes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.need(DetailSection.Quotes)
        advanceUntilIdle()

        repository.quotesError = DomainError.Timeout
        viewModel.onIntent(DetailIntent.LoadMoreClicked(DetailSection.Quotes))
        advanceUntilIdle()

        val quotes = checkNotNull(viewModel.state.value.quotes.dataOrNull)
        assertEquals(25, quotes.items.size)
        assertTrue(quotes.loadMoreFailed)
        assertFalse(quotes.isLoadingMore)
    }

    @Test
    fun `a missing novel is an error and retry reloads`() = runTest {
        val saved = repository.novels.remove("v2002")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(
            UiText.StringResource(StringResource.error_not_found),
            viewModel.state.value.error?.message,
        )

        repository.novels["v2002"] = checkNotNull(saved)
        viewModel.onIntent(DetailIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
        assertNotNull(viewModel.state.value.hero)
    }

    @Test
    fun `synopsis toggles`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(DetailIntent.SynopsisToggled)
        assertTrue(viewModel.state.value.isSynopsisExpanded)
    }

    @Test
    fun `actions become effects`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(DetailIntent.BackClicked)
            assertEquals(DetailEffect.NavigateBack, awaitItem())
            viewModel.onIntent(DetailIntent.ShareClicked)
            assertEquals(DetailEffect.Share("STEINS;GATE", "https://vndb.org/v2002"), awaitItem())
            viewModel.onIntent(DetailIntent.OpenInBrowserClicked)
            assertEquals(DetailEffect.OpenUrl("https://vndb.org/v2002"), awaitItem())
            viewModel.onIntent(DetailIntent.LinkClicked("https://en.wikipedia.org"))
            assertEquals(DetailEffect.OpenUrl("https://en.wikipedia.org"), awaitItem())
            viewModel.onIntent(DetailIntent.RelationClicked("v17102"))
            assertEquals(DetailEffect.NavigateToNovel("v17102"), awaitItem())
            viewModel.onIntent(DetailIntent.CharacterClicked("c6487"))
            assertEquals(DetailEffect.NavigateToCharacter("c6487"), awaitItem())
            viewModel.onIntent(DetailIntent.TagClicked("g249"))
            assertEquals(DetailEffect.NavigateToTag("g249"), awaitItem())
            viewModel.onIntent(DetailIntent.EditionClicked("r7668"))
            assertEquals(DetailEffect.NavigateToRelease("r7668"), awaitItem())
            viewModel.onIntent(DetailIntent.ReviewsClicked)
            assertEquals(DetailEffect.OpenUrl("https://vndb.org/v2002/reviews"), awaitItem())
        }
    }

    @Test
    fun `the page starts at the spoiler level from settings`() = runTest {
        settings.current.value = AppSettings(defaultSpoilerLevel = SpoilerPreference.Major)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(SpoilerLevel.Major, viewModel.state.value.spoilerLevel)
        assertEquals(UiText.StringValue("Intro. Twist"), viewModel.state.value.synopsis)
    }

    @Test
    fun `a finished VN shows every spoiler when spoil me if finished is on`() = runTest {
        settings.current.value = AppSettings(spoilIfFinished = true)
        sessions.current.value = Session("u2", "Yorhel", canReadPrivateList = true)
        userListRepository.entryLabels["v2002"] = listOf(2, 7)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(SpoilerLevel.Major, viewModel.state.value.spoilerLevel)
        assertTrue("entryLabels:v2002" in userListRepository.calls)
    }

    @Test
    fun `an unfinished VN, or a guest, keeps the default level`() = runTest {
        settings.current.value = AppSettings(spoilIfFinished = true)
        val guest = createViewModel()
        advanceUntilIdle()
        assertEquals(SpoilerLevel.None, guest.state.value.spoilerLevel)
        assertTrue(userListRepository.calls.isEmpty())

        sessions.current.value = Session("u2", "Yorhel", canReadPrivateList = true)
        userListRepository.entryLabels["v2002"] = listOf(1)
        val playing = createViewModel()
        advanceUntilIdle()
        assertEquals(SpoilerLevel.None, playing.state.value.spoilerLevel)
    }

    @Test
    fun `sensitive content starts with every screenshot level and lists sexual tags`() = runTest {
        settings.current.value = AppSettings(showSensitiveContent = true)
        repository.novels["v2002"] = DetailSamples.novel(
            tags = listOf(
                DetailSamples.tag("Visible"),
                DetailSamples.tag("Nukige", category = TagCategory.Sexual),
            ),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(DetailIntent.SectionNeeded(DetailSection.Screenshots))
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.screenshots.dataOrNull?.visibleCount)
        assertEquals(
            UiText.StringResource(StringResource.detail_tags_sexual),
            viewModel.state.value.tagGroups.last().title,
        )
    }

    @Test
    fun `guests get no My list card`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.state.value.myList)
    }

    @Test
    fun `signed-in users see their entry, refreshed after an edit, and can open the editor`() =
        runTest {
            sessions.current.value = SessionSamples.session()
            val viewModel = createViewModel()
            advanceUntilIdle()
            with(checkNotNull(viewModel.state.value.myList)) {
                assertFalse(isOnList)
                assertTrue(canEdit)
            }

            userListRepository.saveEntry(
                MyListEntry(
                    novelId = "v2002",
                    labelIds = setOf(ListLabel.PLAYING),
                    vote = 90,
                    labelNames = mapOf(ListLabel.PLAYING to "Playing"),
                ),
                previous = null,
            )
            advanceUntilIdle()
            with(checkNotNull(viewModel.state.value.myList)) {
                assertTrue(isOnList)
                assertEquals(
                    listOf(EntryLabelUIModel(ListLabel.PLAYING, UiText.StringValue("Playing"))),
                    labels,
                )
                assertNotNull(voteLabel)
            }

            viewModel.effect.test {
                viewModel.onIntent(DetailIntent.MyListClicked)
                assertEquals(DetailEffect.OpenListEditor("v2002", "STEINS;GATE"), awaitItem())
            }
        }

    @Test
    fun `a token without listwrite shows the entry read-only`() = runTest {
        sessions.current.value = SessionSamples.session(canWriteList = false)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(checkNotNull(viewModel.state.value.myList).canEdit)
    }
}
