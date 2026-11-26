package com.zc.vndb.feature.browse.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.TagCategory
import com.zc.vndb.common.domain.model.TitleLanguage
import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.usecase.RefreshLanguagesUseCase
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseSamples
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.ProducerInfo
import com.zc.vndb.feature.browse.domain.model.ProducerType
import com.zc.vndb.feature.browse.domain.model.StaffGender
import com.zc.vndb.feature.browse.domain.model.StaffInfo
import com.zc.vndb.feature.browse.domain.model.TagInfo
import com.zc.vndb.feature.browse.domain.repository.FakeBrowseRepository
import com.zc.vndb.feature.browse.domain.usecase.GetProducerUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetStaffUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetTagUseCase
import com.zc.vndb.feature.browse.domain.usecase.SearchNovelsUseCase
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.FilterDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class BrowseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBrowseRepository().apply {
        novels = (1..45).map { BrowseSamples.novel("v$it") }
    }

    private val settings = FakeSettingsRepository()
    private val languages = FakeLanguageRepository()

    private fun createViewModel(scope: BrowseScope = BrowseScope.None) = BrowseViewModel(
        scope = scope,
        searchNovels = SearchNovelsUseCase(repository),
        getTag = GetTagUseCase(repository),
        getProducer = GetProducerUseCase(repository),
        getStaff = GetStaffUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
        observeLanguages = ObserveLanguagesUseCase(languages),
        refreshLanguages = RefreshLanguagesUseCase(languages),
    )

    /** Opens the sheet, edits the draft and taps Apply, as the user would. */
    private fun TestScope.applyFilters(
        viewModel: BrowseViewModel,
        edit: (FilterDraft) -> FilterDraft,
    ) {
        viewModel.onIntent(BrowseIntent.FiltersOpened)
        val draft = assertNotNull(viewModel.state.value.filterSheet).draft
        viewModel.onIntent(BrowseIntent.FilterDraftChanged(edit(draft)))
        viewModel.onIntent(BrowseIntent.FiltersApplied)
        advanceUntilIdle()
    }

    @Test
    fun `loads the first page with the default filter`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(BrowseFilter(), repository.requests.single().filter)
        assertEquals(20, state.results.size)
        assertEquals(
            UiText.StringPlural(PluralResource.browse_found_titles, 45, listOf("%,d".format(45))),
            state.resultCountLabel,
        )
        assertTrue(state.canLoadMore)
        assertEquals(BrowseSort.Popularity, state.sort)
    }

    @Test
    fun `typing searches after a pause, switching to relevance`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.QueryChanged("ste"))
        viewModel.onIntent(BrowseIntent.QueryChanged("steins"))
        advanceTimeBy(399)
        runCurrent()
        assertEquals(1, repository.requests.size)
        assertEquals("steins", viewModel.state.value.query)

        advanceTimeBy(2)
        advanceUntilIdle()
        assertEquals(2, repository.requests.size)
        assertEquals(
            BrowseFilter(query = "steins", sort = BrowseSort.Relevance),
            repository.requests.last().filter,
        )
        assertEquals(BrowseSort.Relevance, viewModel.state.value.sort)
    }

    @Test
    fun `submitting searches at once and clearing restores popularity`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.QueryChanged("steins"))
        viewModel.onIntent(BrowseIntent.SearchSubmitted)
        runCurrent()
        assertEquals("steins", repository.requests.last().filter.query)

        viewModel.onIntent(BrowseIntent.QueryCleared)
        advanceUntilIdle()
        assertEquals(BrowseFilter(), repository.requests.last().filter)
        assertEquals("", viewModel.state.value.query)
        assertEquals(3, repository.requests.size)
    }

    @Test
    fun `filters reload from the first page`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        applyFilters(viewModel) {
            it.copy(ageRating = AgeRating.Adult, genres = setOf(Genre.Mystery))
        }
        viewModel.onIntent(BrowseIntent.SortSelected(BrowseSort.Rating))
        advanceUntilIdle()

        val last = repository.requests.last()
        assertEquals(
            BrowseFilter(
                ageRating = AgeRating.Adult,
                genres = setOf(Genre.Mystery),
                sort = BrowseSort.Rating,
            ),
            last.filter,
        )
        assertEquals(1, last.page)
        assertEquals(
            listOf(ActiveFilterKey.Age, ActiveFilterKey.GenreFilter(Genre.Mystery)),
            viewModel.state.value.activeFilters.map { it.key },
        )

        viewModel.onIntent(
            BrowseIntent.ActiveFilterRemoved(ActiveFilterKey.GenreFilter(Genre.Mystery)),
        )
        advanceUntilIdle()
        assertTrue(repository.requests.last().filter.genres.isEmpty())
    }

    @Test
    fun `selecting the current filter again does not reload`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        applyFilters(viewModel) { it }
        viewModel.onIntent(BrowseIntent.SortSelected(BrowseSort.Popularity))
        advanceUntilIdle()

        assertEquals(1, repository.requests.size)
    }

    @Test
    fun `the direction toggle reverses the sort and picking a sort resets it`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isSortAscending)

        viewModel.onIntent(BrowseIntent.SortDirectionToggled)
        advanceUntilIdle()

        assertTrue(repository.requests.last().filter.isReversed)
        assertTrue(viewModel.state.value.isSortAscending)

        viewModel.onIntent(BrowseIntent.SortSelected(BrowseSort.Title))
        advanceUntilIdle()

        assertFalse(repository.requests.last().filter.isReversed)
        assertTrue(viewModel.state.value.isSortAscending)

        viewModel.onIntent(BrowseIntent.SortDirectionToggled)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSortAscending)
    }

    @Test
    fun `reaching the end appends pages until there are no more`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(40, viewModel.state.value.results.size)

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(45, viewModel.state.value.results.size)
        assertFalse(viewModel.state.value.canLoadMore)

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(listOf(1, 2, 3), repository.requests.map { it.page })
    }

    @Test
    fun `a failed next page waits for an explicit retry`() = runTest {
        repository.errorOnPage = 2
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.loadMoreFailed)
        assertEquals(20, viewModel.state.value.results.size)

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(2, repository.requests.size)

        repository.errorOnPage = null
        viewModel.onIntent(BrowseIntent.LoadMoreRetried)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.loadMoreFailed)
        assertEquals(40, viewModel.state.value.results.size)
    }

    @Test
    fun `a failed first page shows an error and retry reloads`() = runTest {
        repository.error = DomainError.Network
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(StringResource.error_network),
            viewModel.state.value.error?.message,
        )
        assertTrue(viewModel.state.value.results.isEmpty())

        repository.error = null
        viewModel.onIntent(BrowseIntent.Retry)
        advanceUntilIdle()
        assertEquals(null, viewModel.state.value.error)
        assertEquals(20, viewModel.state.value.results.size)
    }

    @Test
    fun `no matches shows the empty state`() = runTest {
        repository.novels = emptyList()
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.showEmpty)
    }

    @Test
    fun `clicking a result opens it`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(BrowseIntent.NovelClicked("v7"))
            assertEquals(BrowseEffect.NavigateToNovel("v7"), awaitItem())
        }
    }

    @Test
    fun `a tag page scopes the list to the tag and shows its header`() = runTest {
        repository.tags["g249"] = TagInfo(
            id = "g249",
            name = "Time Travel",
            aliases = listOf("Time Loop"),
            description = "Moving between [url=/g1]points[/url] in time.",
            category = TagCategory.Content,
            novelCount = 1067,
        )
        val viewModel = createViewModel(BrowseScope.Tag("g249"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isScopedPage)
        assertEquals(UiText.StringValue("Time Travel"), state.header?.name)
        assertEquals(BrowseScope.Tag("g249"), repository.requests.single().filter.scope)

        applyFilters(viewModel) { it.copy(genres = setOf(Genre.Mystery)) }
        assertEquals(BrowseScope.Tag("g249"), repository.requests.last().filter.scope)
    }

    @Test
    fun `a tag page holds the header's place until the tag loads`() = runTest {
        repository.tags["g249"] = TagInfo(
            id = "g249",
            name = "Time Travel",
            aliases = emptyList(),
            description = null,
            category = TagCategory.Content,
            novelCount = 1067,
        )
        val viewModel = createViewModel(BrowseScope.Tag("g249"))

        assertTrue(viewModel.state.value.isHeaderLoading)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isHeaderLoading)
        assertEquals(UiText.StringValue("Time Travel"), viewModel.state.value.header?.name)
    }

    @Test
    fun `a failed tag lookup still lists the tag's novels`() = runTest {
        val viewModel = createViewModel(BrowseScope.Tag("g-missing"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(null, state.header)
        assertFalse(state.isHeaderLoading)
        assertEquals(20, state.results.size)
        assertEquals(BrowseScope.Tag("g-missing"), repository.requests.single().filter.scope)
    }

    @Test
    fun `the Browse tab is not a tag page`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isScopedPage)
        assertFalse(viewModel.state.value.isHeaderLoading)
        assertEquals(BrowseScope.None, repository.requests.single().filter.scope)
    }

    @Test
    fun `the top rated page opens on rating and shows its header without loading one`() = runTest {
        val viewModel = createViewModel(BrowseScope.TopRated)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isScopedPage)
        assertEquals(
            UiText.StringResource(StringResource.browse_top_rated_title),
            state.header?.name,
        )
        assertEquals(BrowseSort.Rating, state.sort)
        val filter = repository.requests.single().filter
        assertEquals(BrowseScope.TopRated, filter.scope)
        assertEquals(BrowseSort.Rating, filter.sort)
    }

    @Test
    fun `a producer page opens on newest and shows the producer header`() = runTest {
        repository.producers["p30"] = ProducerInfo(
            id = "p30",
            name = "Nitroplus",
            originalName = null,
            aliases = emptyList(),
            type = ProducerType.Company,
            language = "ja",
            description = null,
        )
        val viewModel = createViewModel(BrowseScope.Producer("p30"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isScopedPage)
        assertEquals(UiText.StringValue("Nitroplus"), state.header?.name)
        assertEquals(BrowseSort.Newest, state.sort)
        val filter = repository.requests.single().filter
        assertEquals(BrowseScope.Producer("p30"), filter.scope)
        assertEquals(BrowseSort.Newest, filter.sort)

        applyFilters(viewModel) { it.copy(genres = setOf(Genre.Mystery)) }
        assertEquals(BrowseScope.Producer("p30"), repository.requests.last().filter.scope)
    }

    @Test
    fun `a failed producer lookup still lists the producer's novels`() = runTest {
        val viewModel = createViewModel(BrowseScope.Producer("p-missing"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(null, state.header)
        assertEquals(20, state.results.size)
    }

    @Test
    fun `searching on a producer page switches to relevance and clearing returns to newest`() =
        runTest {
            val viewModel = createViewModel(BrowseScope.Producer("p30"))
            advanceUntilIdle()

            viewModel.onIntent(BrowseIntent.QueryChanged("fate"))
            viewModel.onIntent(BrowseIntent.SearchSubmitted)
            advanceUntilIdle()
            assertEquals(BrowseSort.Relevance, repository.requests.last().filter.sort)

            viewModel.onIntent(BrowseIntent.QueryCleared)
            advanceUntilIdle()
            assertEquals(BrowseSort.Newest, repository.requests.last().filter.sort)
        }

    @Test
    fun `a staff page opens on newest and shows the staff header`() = runTest {
        repository.staff["s2"] = StaffInfo(
            id = "s2",
            name = "Nasu Kinoko",
            originalName = null,
            aliases = emptyList(),
            language = "ja",
            gender = StaffGender.Male,
            description = null,
            links = emptyList(),
        )
        val viewModel = createViewModel(BrowseScope.Staff("s2"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isScopedPage)
        assertEquals(UiText.StringValue("Nasu Kinoko"), state.header?.name)
        assertEquals(1, state.header?.links?.size)
        assertEquals(BrowseSort.Newest, state.sort)
        val filter = repository.requests.single().filter
        assertEquals(BrowseScope.Staff("s2"), filter.scope)
        assertEquals(BrowseSort.Newest, filter.sort)
    }

    @Test
    fun `a failed staff lookup still lists the person's novels`() = runTest {
        val viewModel = createViewModel(BrowseScope.Staff("s-missing"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(null, state.header)
        assertEquals(20, state.results.size)
    }

    @Test
    fun `a link click opens the url`() = runTest {
        val viewModel = createViewModel(BrowseScope.Staff("s2"))
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(BrowseIntent.LinkClicked("https://vndb.org/s2"))
            assertEquals(BrowseEffect.OpenUrl("https://vndb.org/s2"), awaitItem())
        }
    }

    @Test
    fun `back and description toggle`() = runTest {
        val viewModel = createViewModel(BrowseScope.Tag("g249"))
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.DescriptionToggled)
        assertTrue(viewModel.state.value.isDescriptionExpanded)
        viewModel.effect.test {
            viewModel.onIntent(BrowseIntent.BackClicked)
            assertEquals(BrowseEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `pull to refresh reloads the first page of the current filter`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(40, viewModel.state.value.results.size)
        repository.novels = (100..150).map { BrowseSamples.novel("v$it") }

        viewModel.onIntent(BrowseIntent.Refresh)
        assertTrue(viewModel.state.value.isRefreshing)
        assertEquals(40, viewModel.state.value.results.size)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isRefreshing)
        assertEquals(1, repository.requests.last().page)
        assertEquals("v100", state.results.first().id)
        assertEquals(20, state.results.size)

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()
        assertEquals(2, repository.requests.last().page)
    }

    @Test
    fun `a failed refresh keeps the results and shows an error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.error = DomainError.Timeout

        viewModel.effect.test {
            viewModel.onIntent(BrowseIntent.Refresh)
            val effect = awaitItem()
            assertTrue(effect is BrowseEffect.ShowError)
        }
        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals(20, viewModel.state.value.results.size)
    }

    @Test
    fun `original script titles come first when chosen`() = runTest {
        settings.current.value = AppSettings(titleLanguage = TitleLanguage.Original)
        repository.novels = listOf(
            BrowseSamples.novel("v1").copy(title = "Steins;Gate", originalTitle = "シュタインズ・ゲート"),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            UiText.StringValue("シュタインズ・ゲート"),
            viewModel.state.value.results.single().title,
        )
    }
}
