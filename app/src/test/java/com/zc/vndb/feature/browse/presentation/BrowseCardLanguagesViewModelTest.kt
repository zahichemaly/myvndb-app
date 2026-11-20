package com.zc.vndb.feature.browse.presentation

import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.usecase.RefreshLanguagesUseCase
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.browse.domain.model.BrowseSamples
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.repository.FakeBrowseRepository
import com.zc.vndb.feature.browse.domain.usecase.GetProducerUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetStaffUseCase
import com.zc.vndb.feature.browse.domain.usecase.GetTagUseCase
import com.zc.vndb.feature.browse.domain.usecase.SearchNovelsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

/** The language pills on Browse cards: made from the Settings choice, with no extra requests. */
class BrowseCardLanguagesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBrowseRepository().apply {
        novels = (1..45).map { BrowseSamples.novel("v$it", languages = listOf("ja", "en", "ko")) } +
            BrowseSamples.novel("v-ja-only", languages = listOf("ja"))
    }
    private val settings = FakeSettingsRepository()
    private val languages = FakeLanguageRepository()

    private fun createViewModel() = BrowseViewModel(
        scope = BrowseScope.None,
        searchNovels = SearchNovelsUseCase(repository),
        getTag = GetTagUseCase(repository),
        getProducer = GetProducerUseCase(repository),
        getStaff = GetStaffUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
        observeLanguages = ObserveLanguagesUseCase(languages),
        refreshLanguages = RefreshLanguagesUseCase(languages),
    )

    @Test
    fun `cards have no pills until languages are chosen`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.results.all { it.languagePills.isEmpty() })
        assertEquals(listOf("ja", "en", "ko"), viewModel.state.value.results.first().languageCodes)
    }

    @Test
    fun `chosen languages show as pills on the novels that have them`() = runTest {
        settings.current.value = AppSettings(cardLanguages = listOf("en", "ko"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val first = viewModel.state.value.results.first()
        assertEquals(listOf("en", "ko"), first.languagePills.map { it.code })
        assertEquals("🇬🇧 English", first.languagePills.first().label)
    }

    @Test
    fun `later pages get pills too`() = runTest {
        settings.current.value = AppSettings(cardLanguages = listOf("en"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(BrowseIntent.EndReached)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.results.size > 20)
        assertTrue(viewModel.state.value.results.all { it.languagePills.size == 1 })
    }

    @Test
    fun `changing the setting redraws the pills without asking the API again`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val requests = repository.requests.size
        assertTrue(viewModel.state.value.results.all { it.languagePills.isEmpty() })

        settings.current.value = AppSettings(cardLanguages = listOf("ja"))
        advanceUntilIdle()

        assertTrue(
            viewModel.state.value.results.all {
                it.languagePills.map { p -> p.code } == listOf(
                    "ja",
                )
            },
        )
        assertEquals(requests, repository.requests.size)

        settings.current.value = AppSettings(cardLanguages = emptyList())
        advanceUntilIdle()

        assertTrue(viewModel.state.value.results.all { it.languagePills.isEmpty() })
        assertEquals(requests, repository.requests.size)
    }

    @Test
    fun `a novel without the chosen language gets no pill`() = runTest {
        repository.novels = listOf(BrowseSamples.novel("v1", languages = listOf("ja")))
        settings.current.value = AppSettings(cardLanguages = listOf("en"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.results.single().languagePills.isEmpty())
    }

    @Test
    fun `a fetched language list renames the pills`() = runTest {
        settings.current.value = AppSettings(cardLanguages = listOf("zh-Hans"))
        repository.novels = listOf(BrowseSamples.novel("v1", languages = listOf("ja", "zh-Hans")))
        val viewModel = createViewModel()
        advanceUntilIdle()

        languages.current.value = listOf(LanguageOption("zh-Hans", "Chinese (simplified)"))
        advanceUntilIdle()

        assertEquals(
            "Chinese (simplified)",
            viewModel.state.value.results.single().languagePills.single().name,
        )
    }
}
