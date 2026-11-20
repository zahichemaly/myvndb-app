package com.zc.vndb.feature.browse.presentation

import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.browse.domain.model.AdvancedFilterKind
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseSamples
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import com.zc.vndb.feature.browse.domain.repository.FakeBrowseRepository
import com.zc.vndb.feature.browse.domain.usecase.GetProducerUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetStaffUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetTagUseCase
import com.zc.vndb.feature.browse.domain.usecase.SearchNovelsUseCase
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.FilterDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

/** The filter sheet, the pills it leaves on Browse, and how Browse follows the 18+ setting. */
class BrowseFiltersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBrowseRepository().apply {
        novels = (1..45).map { BrowseSamples.novel("v$it") }
    }

    private val settings = FakeSettingsRepository()

    private fun createViewModel() = BrowseViewModel(
        scope = BrowseScope.None,
        searchNovels = SearchNovelsUseCase(repository),
        getTag = GetTagUseCase(repository),
        getProducer = GetProducerUseCase(repository),
        getStaff = GetStaffUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
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

    private val BrowseViewModel.pillKeys get() = state.value.activeFilters.map { it.key }

    @Test
    fun `nothing applied means no pills`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.activeFilters.isEmpty())
        assertNull(viewModel.state.value.filterSheet)
    }

    @Test
    fun `opening the sheet starts from the applied filters and calls no API`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val requests = repository.requests.size

        viewModel.onIntent(BrowseIntent.FiltersOpened)
        advanceUntilIdle()

        assertEquals(FilterDraft(AgeRating.AllAges), viewModel.state.value.filterSheet?.draft)
        assertEquals(requests, repository.requests.size)
    }

    @Test
    fun `editing the draft calls no API and applies nothing`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val requests = repository.requests.size
        val draft = FilterDraft(
            ageRating = AgeRating.Adult,
            genres = setOf(Genre.Horror),
            advanced = AdvancedFilters(supportedLanguages = setOf("en")),
        )

        viewModel.onIntent(BrowseIntent.FiltersOpened)
        viewModel.onIntent(BrowseIntent.FilterDraftChanged(draft))
        advanceUntilIdle()

        assertEquals(draft, viewModel.state.value.filterSheet?.draft)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
        assertEquals(requests, repository.requests.size)
    }

    @Test
    fun `apply closes the sheet and reloads the list with the draft`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val draftAdvanced = AdvancedFilters(
            supportedLanguages = setOf("ja", "en"),
            lengths = setOf(NovelLength.Short),
            era = ReleaseEra.Since2020,
        )

        applyFilters(viewModel) {
            it.copy(
                ageRating = AgeRating.Any,
                genres = setOf(Genre.Mystery),
                advanced = draftAdvanced,
            )
        }

        val state = viewModel.state.value
        assertNull(state.filterSheet)
        val request = repository.requests.last()
        assertEquals(AgeRating.Any, request.filter.ageRating)
        assertEquals(setOf(Genre.Mystery), request.filter.genres)
        assertEquals(draftAdvanced, request.filter.advanced)
        assertEquals(20, request.pageSize)
        assertEquals(1, request.page)
        assertEquals(
            listOf(
                ActiveFilterKey.Age,
                ActiveFilterKey.GenreFilter(Genre.Mystery),
                ActiveFilterKey.Advanced(AdvancedFilterKind.SupportedLanguages),
                ActiveFilterKey.Advanced(AdvancedFilterKind.Length),
                ActiveFilterKey.Advanced(AdvancedFilterKind.Era),
            ),
            state.activeFilters.map { it.key },
        )
    }

    @Test
    fun `closing the sheet drops the draft`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val requests = repository.requests.size

        viewModel.onIntent(BrowseIntent.FiltersOpened)
        viewModel.onIntent(
            BrowseIntent.FilterDraftChanged(FilterDraft(AgeRating.Adult, setOf(Genre.Horror))),
        )
        viewModel.onIntent(BrowseIntent.FiltersDismissed)
        advanceUntilIdle()

        assertNull(viewModel.state.value.filterSheet)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
        assertEquals(requests, repository.requests.size)
    }

    @Test
    fun `reopening the sheet starts from what was applied`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        applyFilters(viewModel) {
            it.copy(
                genres = setOf(Genre.Comedy),
                advanced = AdvancedFilters(originalLanguage = "ja"),
            )
        }

        viewModel.onIntent(BrowseIntent.FiltersOpened)

        assertEquals(
            FilterDraft(
                AgeRating.AllAges,
                setOf(Genre.Comedy),
                AdvancedFilters(originalLanguage = "ja"),
            ),
            viewModel.state.value.filterSheet?.draft,
        )
    }

    @Test
    fun `reset puts the draft back to the starting filters`() = runTest {
        settings.current.value = AppSettings(includeAdultInBrowse = true)
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(BrowseIntent.FiltersOpened)
        viewModel.onIntent(
            BrowseIntent.FilterDraftChanged(FilterDraft(AgeRating.Adult, setOf(Genre.Horror))),
        )

        viewModel.onIntent(BrowseIntent.FiltersReset)

        assertEquals(FilterDraft(AgeRating.Any), viewModel.state.value.filterSheet?.draft)
    }

    @Test
    fun `the age rating shows as a pill only when it is not the starting one`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        applyFilters(viewModel) { it.copy(ageRating = AgeRating.AllAges) }
        assertTrue(viewModel.state.value.activeFilters.isEmpty())

        applyFilters(viewModel) { it.copy(ageRating = AgeRating.Adult) }
        assertEquals(
            listOf(ActiveFilterKey.Age),
            viewModel.state.value.activeFilters.map { it.key },
        )

        viewModel.onIntent(BrowseIntent.ActiveFilterRemoved(ActiveFilterKey.Age))
        advanceUntilIdle()
        assertEquals(AgeRating.AllAges, repository.requests.last().filter.ageRating)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `a pill removes just its filter`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        applyFilters(viewModel) {
            it.copy(
                genres = setOf(Genre.Mystery, Genre.Horror),
                advanced = AdvancedFilters(
                    supportedLanguages = setOf("en"),
                    era = ReleaseEra.In2000s,
                ),
            )
        }

        viewModel.onIntent(
            BrowseIntent.ActiveFilterRemoved(
                ActiveFilterKey.Advanced(AdvancedFilterKind.SupportedLanguages),
            ),
        )
        advanceUntilIdle()

        val filter = repository.requests.last().filter
        assertEquals(setOf(Genre.Mystery, Genre.Horror), filter.genres)
        assertEquals(AdvancedFilters(era = ReleaseEra.In2000s), filter.advanced)
        assertEquals(3, viewModel.state.value.activeFilters.size)
    }

    @Test
    fun `clear all goes back to the starting filters but keeps the search`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(BrowseIntent.QueryChanged("fate"))
        viewModel.onIntent(BrowseIntent.SearchSubmitted)
        applyFilters(viewModel) {
            it.copy(
                ageRating = AgeRating.Adult,
                genres = setOf(Genre.Fantasy),
                advanced = AdvancedFilters(lengths = setOf(NovelLength.Long)),
            )
        }

        viewModel.onIntent(BrowseIntent.ActiveFiltersCleared)
        advanceUntilIdle()

        val filter = repository.requests.last().filter
        assertEquals("fate", filter.query)
        assertEquals(AgeRating.AllAges, filter.ageRating)
        assertTrue(filter.genres.isEmpty())
        assertTrue(filter.advanced.isEmpty)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `include 18+ in Browse starts with both ages`() = runTest {
        settings.current.value = AppSettings(includeAdultInBrowse = true)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(AgeRating.Any, repository.requests.single().filter.ageRating)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `turning on 18+ in Settings updates Browse while it is open`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(AgeRating.AllAges, repository.requests.last().filter.ageRating)

        settings.current.value = AppSettings(includeAdultInBrowse = true)
        advanceUntilIdle()

        assertEquals(AgeRating.Any, repository.requests.last().filter.ageRating)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `turning 18+ off in Settings goes back to all ages`() = runTest {
        settings.current.value = AppSettings(includeAdultInBrowse = true)
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(AgeRating.Any, repository.requests.last().filter.ageRating)

        settings.current.value = AppSettings(includeAdultInBrowse = false)
        advanceUntilIdle()

        assertEquals(AgeRating.AllAges, repository.requests.last().filter.ageRating)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `changing the setting reloads from the API and replaces a picked age rating`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        applyFilters(viewModel) { it.copy(ageRating = AgeRating.Adult) }
        val requests = repository.requests.size

        settings.current.value = AppSettings(includeAdultInBrowse = true)
        advanceUntilIdle()

        assertTrue(repository.requests.size > requests)
        assertEquals(AgeRating.Any, repository.requests.last().filter.ageRating)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `changing the setting reloads even when the age rating already matches`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        applyFilters(viewModel) { it.copy(ageRating = AgeRating.Any) }
        val requests = repository.requests.size

        settings.current.value = AppSettings(includeAdultInBrowse = true)
        advanceUntilIdle()

        assertTrue(repository.requests.size > requests)
        assertTrue(viewModel.state.value.activeFilters.isEmpty())
    }

    @Test
    fun `changing the setting keeps the search and the other filters`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onIntent(BrowseIntent.QueryChanged("fate"))
        viewModel.onIntent(BrowseIntent.SearchSubmitted)
        applyFilters(viewModel) { it.copy(genres = setOf(Genre.Fantasy)) }

        settings.current.value = AppSettings(includeAdultInBrowse = true)
        advanceUntilIdle()

        val filter = repository.requests.last().filter
        assertEquals("fate", filter.query)
        assertEquals(setOf(Genre.Fantasy), filter.genres)
        assertEquals(AgeRating.Any, filter.ageRating)
    }
}
