package com.zc.vndb.feature.character.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.CharacterRole
import com.zc.vndb.common.domain.model.SpoilerPreference
import com.zc.vndb.common.domain.model.VoiceActor
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.character.domain.model.CharacterSamples
import com.zc.vndb.feature.character.domain.model.Sex
import com.zc.vndb.feature.character.domain.repository.FakeCharacterRepository
import com.zc.vndb.feature.character.domain.usecase.GetCharacterUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class CharacterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCharacterRepository().apply {
        characters["c6487"] = CharacterSamples.profile(
            description = "Researcher. [spoiler]Twist[/spoiler]",
            spoilerSex = Sex.Male,
            traits = listOf(
                CharacterSamples.trait("Long"),
                CharacterSamples.trait("Secret", spoiler = 2),
                CharacterSamples.trait("Explicit", group = "Engages in (Sexual)", sexual = true),
            ),
            appearances = listOf(
                CharacterSamples.appearance("v2002", CharacterRole.Primary),
                CharacterSamples.appearance("v5883", CharacterRole.Appears, spoiler = 1),
            ),
        )
        voiceActors = mapOf("v2002" to VoiceActor("s240", "Imai Asami"))
    }

    private val settings = FakeSettingsRepository()

    private fun createViewModel() = CharacterViewModel(
        characterId = "c6487",
        getCharacter = GetCharacterUseCase(repository),
        observeSettings = ObserveSettingsUseCase(settings),
    )

    @Test
    fun `loads the character with spoilers hidden`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertEquals(UiText.StringValue("Makise Kurisu"), hero?.name)
            assertEquals(
                listOf<UiText>(UiText.StringValue("Assistant"), UiText.StringValue("Christina")),
                aliases,
            )
            assertEquals(UiText.StringValue("Researcher."), description)
            assertTrue(hero?.spoilerIdentity.orEmpty().isEmpty())
            assertEquals(listOf("v2002"), appearances.map { it.novelId })
            assertEquals(
                UiText.StringResource(StringResource.detail_cv, listOf("Imai Asami")),
                appearances.single().voiceActorLabel,
            )
            assertNotNull(hiddenTraitsLabel)
            assertNotNull(hiddenAppearancesLabel)
        }
    }

    @Test
    fun `Major reveals spoilers`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(CharacterIntent.SpoilerLevelSelected(SpoilerLevel.Major))

        with(viewModel.state.value) {
            assertEquals(UiText.StringValue("Researcher. Twist"), description)
            assertEquals(1, hero?.spoilerIdentity?.size)
            assertEquals(listOf("v2002", "v5883"), appearances.map { it.novelId })
            assertNull(hiddenTraitsLabel)
            assertNull(hiddenAppearancesLabel)
        }
    }

    @Test
    fun `a missing character is an error and retry reloads`() = runTest {
        val saved = repository.characters.remove("c6487")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(
            UiText.StringResource(StringResource.error_not_found),
            viewModel.state.value.error?.message,
        )

        repository.characters["c6487"] = checkNotNull(saved)
        viewModel.onIntent(CharacterIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
        assertNotNull(viewModel.state.value.hero)
    }

    @Test
    fun `description toggles and actions become effects`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(CharacterIntent.DescriptionToggled)
        assertTrue(viewModel.state.value.isDescriptionExpanded)

        viewModel.effect.test {
            viewModel.onIntent(CharacterIntent.BackClicked)
            assertEquals(CharacterEffect.NavigateBack, awaitItem())
            viewModel.onIntent(CharacterIntent.ShareClicked)
            assertEquals(
                CharacterEffect.Share("Makise Kurisu", "https://vndb.org/c6487"),
                awaitItem(),
            )
            viewModel.onIntent(CharacterIntent.OpenInBrowserClicked)
            assertEquals(CharacterEffect.OpenUrl("https://vndb.org/c6487"), awaitItem())
            viewModel.onIntent(CharacterIntent.NovelClicked("v2002"))
            assertEquals(CharacterEffect.NavigateToNovel("v2002"), awaitItem())
        }
    }

    @Test
    fun `the page starts at the spoiler level from settings`() = runTest {
        settings.current.value = AppSettings(defaultSpoilerLevel = SpoilerPreference.Minor)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(SpoilerLevel.Minor, viewModel.state.value.spoilerLevel)
    }

    @Test
    fun `sexual traits stay hidden until sensitive content is shown`() = runTest {
        val hidden = createViewModel()
        advanceUntilIdle()
        hidden.onIntent(CharacterIntent.SpoilerLevelSelected(SpoilerLevel.Major))

        val hiddenTraits = hidden.state.value.traitGroups.flatMap { it.traits }
        assertFalse(UiText.StringValue("Explicit") in hiddenTraits)
        assertNull(hidden.state.value.hiddenTraitsLabel)

        settings.current.value = AppSettings(showSensitiveContent = true)
        val shown = createViewModel()
        advanceUntilIdle()

        val shownTraits = shown.state.value.traitGroups.flatMap { it.traits }
        assertTrue(UiText.StringValue("Explicit") in shownTraits)
    }

    @Test
    fun `refresh reloads behind the pull indicator without a loading state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.characters["c6487"] = CharacterSamples.profile(description = "Updated bio")

        viewModel.state.test {
            skipItems(1)
            viewModel.onIntent(CharacterIntent.Refresh)
            val refreshing = awaitItem()
            assertTrue(refreshing.isRefreshing)
            assertFalse(refreshing.isLoading)
            assertEquals(UiText.StringValue("Researcher."), refreshing.description)

            cancelAndConsumeRemainingEvents()
        }
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(isRefreshing)
            assertEquals(UiText.StringValue("Updated bio"), description)
        }
    }

    @Test
    fun `a failed refresh keeps the character and shows an error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.characters.remove("c6487")

        viewModel.effect.test {
            viewModel.onIntent(CharacterIntent.Refresh)
            advanceUntilIdle()
            assertEquals(
                UiText.StringResource(StringResource.error_not_found),
                (awaitItem() as CharacterEffect.ShowError).error.message,
            )
        }
        with(viewModel.state.value) {
            assertFalse(isRefreshing)
            assertNull(error)
            assertNotNull(hero)
        }
    }
}
