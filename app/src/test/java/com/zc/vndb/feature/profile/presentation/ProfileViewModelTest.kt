package com.zc.vndb.feature.profile.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.AppSettings
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.common.domain.model.SessionSamples
import com.zc.vndb.common.domain.repository.FakeLanguageRepository
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.common.domain.repository.FakeSettingsRepository
import com.zc.vndb.common.domain.usecase.ObserveLanguagesUseCase
import com.zc.vndb.common.domain.usecase.ObserveSessionUseCase
import com.zc.vndb.common.domain.usecase.ObserveSettingsUseCase
import com.zc.vndb.common.domain.usecase.UpdateSettingsUseCase
import com.zc.vndb.common.domain.util.FixedClock
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.profile.domain.repository.FakeProfileRepository
import com.zc.vndb.feature.profile.domain.usecase.GetProfileUseCase
import com.zc.vndb.feature.userlist.domain.model.EntryOrder
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.model.UserListSamples
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import com.zc.vndb.feature.userlist.domain.usecase.GetListEntriesUseCase
import com.zc.vndb.feature.userlist.domain.usecase.GetListLabelsUseCase
import com.zc.vndb.feature.userlist.domain.usecase.ObserveListChangesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sessions = FakeSessionRepository()
    private val userList = FakeUserListRepository().apply {
        labels = listOf(
            UserListSamples.label(ListLabel.FINISHED, count = 2),
            UserListSamples.label(ListLabel.PLAYING, count = 30),
        )
        entries[ListLabel.PLAYING] = (1..30).map { UserListSamples.entry("v$it") }
        entries[ListLabel.FINISHED] = listOf(
            UserListSamples.entry("v100"),
            UserListSamples.entry("v101"),
        )
    }

    private val profile = FakeProfileRepository(userList.calls)

    private val settings = FakeSettingsRepository()
    private val languages = FakeLanguageRepository()

    private fun createViewModel() = ProfileViewModel(
        observeSession = ObserveSessionUseCase(sessions),
        getProfile = GetProfileUseCase(profile, GetListLabelsUseCase(userList)),
        getListEntries = GetListEntriesUseCase(userList),
        clock = FixedClock(Instant.parse("2026-11-16T00:00:00Z")),
        observeSettings = ObserveSettingsUseCase(settings),
        observeLanguages = ObserveLanguagesUseCase(languages),
        updateSettings = UpdateSettingsUseCase(settings),
        observeListChanges = ObserveListChangesUseCase(userList),
    )

    /** `state` only runs while collected, as on screen. */
    private fun TestScope.collect(state: StateFlow<ProfileState>) {
        backgroundScope.launch { state.collect {} }
    }

    @Test
    fun `a guest sees the log-in prompt and nothing is requested`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertTrue(isSessionKnown)
            assertFalse(isSignedIn)
        }
        assertTrue(userList.calls.isEmpty())

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.LogInClicked)
            assertEquals(ProfileEffect.NavigateToLogin, awaitItem())
        }
    }

    @Test
    fun `signing in loads the labels and Playing's first page`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        sessions.current.value = SessionSamples.session()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(UiText.StringValue("Yorhel"), state.header?.username)
        assertEquals(listOf(ListLabel.PLAYING, ListLabel.FINISHED), state.labels.map { it.id })
        assertEquals(ListLabel.PLAYING, state.labels.single { it.isSelected }.id)
        assertFalse(state.isMultiSelect)
        assertEquals(25, state.entries.items.size)
        assertTrue(state.entries.hasMore)
        assertTrue("entries:1:1" in userList.calls)
    }

    @Test
    fun `the user's labels sit between the statuses and Voted`() = runTest {
        userList.labels = userList.labels +
            UserListSamples.label(ListLabel.VOTED) +
            UserListSamples.label(12, name = "Ready to play")
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        assertEquals(
            listOf(ListLabel.PLAYING, ListLabel.FINISHED, 12, ListLabel.VOTED),
            viewModel.state.value.labels.map { it.id },
        )
    }

    @Test
    fun `single-select picks one chip, and tapping it again shows the whole list`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        viewModel.onIntent(ProfileIntent.LabelSelected(ListLabel.FINISHED))
        advanceUntilIdle()
        assertEquals(
            listOf(ListLabel.FINISHED),
            viewModel.state.value.labels.filter { it.isSelected }.map { it.id },
        )

        viewModel.onIntent(ProfileIntent.LabelSelected(ListLabel.FINISHED))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.labels.none { it.isSelected })
        assertEquals("entries::1", userList.calls.last())
        assertEquals(25, viewModel.state.value.entries.items.size)
    }

    @Test
    fun `multi-select combines chips, and going back keeps the one picked last`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        viewModel.onIntent(ProfileIntent.MultiSelectToggled)
        viewModel.onIntent(ProfileIntent.LabelSelected(ListLabel.FINISHED))
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertTrue(isMultiSelect)
            assertEquals(2, labels.count { it.isSelected })
        }
        assertEquals("entries:1,2:1", userList.calls.last())

        viewModel.onIntent(ProfileIntent.MultiSelectToggled)
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(isMultiSelect)
            assertEquals(ListLabel.FINISHED, labels.single { it.isSelected }.id)
            assertEquals(listOf("v100", "v101"), entries.items.map { it.novelId })
        }
    }

    @Test
    fun `typing searches the list once it pauses, and clearing goes back`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        viewModel.onIntent(ProfileIntent.QueryChanged("v1"))
        viewModel.onIntent(ProfileIntent.QueryChanged("v12"))
        advanceUntilIdle()
        assertEquals("v12", viewModel.state.value.query)
        assertEquals(
            listOf("entries:1:1", "entries:1:1:v12"),
            userList.calls.filter { it.startsWith("entries") },
        )
        assertEquals(listOf("v12"), viewModel.state.value.entries.items.map { it.novelId })

        viewModel.onIntent(ProfileIntent.QueryCleared)
        advanceUntilIdle()
        assertEquals("", viewModel.state.value.query)
        assertEquals(25, viewModel.state.value.entries.items.size)
        assertEquals(2, userList.calls.count { it.startsWith("entries") })
    }

    @Test
    fun `labels load once and are kept, more entries load at the end`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        viewModel.onIntent(ProfileIntent.LabelSelected(ListLabel.FINISHED))
        advanceUntilIdle()
        assertEquals(listOf("v100", "v101"), viewModel.state.value.entries.items.map { it.novelId })

        viewModel.onIntent(ProfileIntent.LabelSelected(ListLabel.PLAYING))
        viewModel.onIntent(ProfileIntent.EndReached)
        advanceUntilIdle()

        assertEquals(30, viewModel.state.value.entries.items.size)
        assertFalse(viewModel.state.value.entries.hasMore)
        assertEquals(
            listOf("entries:1:1", "entries:2:1", "entries:1:2"),
            userList.calls.filter { it.startsWith("entries") },
        )
    }

    @Test
    fun `a failed overview is a retryable error`() = runTest {
        sessions.current.value = SessionSamples.session()
        userList.labelsError = DomainError.Network
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)

        userList.labelsError = null
        viewModel.onIntent(ProfileIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
        assertEquals(25, viewModel.state.value.entries.items.size)
    }

    @Test
    fun `a rejected token signs out with a message`() = runTest {
        sessions.current.value = SessionSamples.session()
        userList.labelsError = DomainError.Unauthorized
        val viewModel = createViewModel()
        collect(viewModel.state)

        viewModel.effect.test {
            advanceUntilIdle()
            assertEquals(
                ProfileEffect.ShowMessage(
                    UiText.StringResource(StringResource.profile_session_expired),
                ),
                awaitItem(),
            )
        }
        // The real repository clears the token; the session flow then turns the screen into a guest.
        sessions.current.value = null
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isSignedIn)
    }

    @Test
    fun `a sign-out elsewhere (Settings) turns the profile into the guest prompt`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        sessions.current.value = null
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSignedIn)
    }

    @Test
    fun `pull to refresh reloads labels and the selected label`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        userList.calls.clear()

        viewModel.onIntent(ProfileIntent.Refresh)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRefreshing)
        assertEquals(25, viewModel.state.value.entries.items.size)
        assertTrue(userList.calls.containsAll(listOf("labels", "user", "entries:1:1")))
    }

    @Test
    fun `entries show pills for the chosen languages`() = runTest {
        settings.current.value = AppSettings(cardLanguages = listOf("en"))
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()

        val items = viewModel.state.value.entries.items
        assertTrue(items.isNotEmpty())
        assertTrue(items.all { card -> card.languagePills.map { it.code } == listOf("en") })
    }

    @Test
    fun `changing the setting redraws the pills without asking the API again`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        val calls = userList.calls.size
        assertTrue(viewModel.state.value.entries.items.all { it.languagePills.isEmpty() })

        settings.current.value = AppSettings(cardLanguages = listOf("ko", "ja"))
        advanceUntilIdle()

        assertTrue(
            viewModel.state.value.entries.items.all { card ->
                card.languagePills.map { it.code } == listOf("ko", "ja")
            },
        )
        assertEquals(calls, userList.calls.size)
    }

    @Test
    fun `lists are A to Z by default and a chosen sort is saved and reloads`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        assertEquals(EntryOrder.Title, userList.requests.last().order)
        assertEquals(
            ListSort.Title,
            viewModel.state.value.sortOptions.single { it.isSelected }.sort,
        )

        viewModel.onIntent(ProfileIntent.SortSelected(ListSort.Updated))
        advanceUntilIdle()

        assertEquals(ListSort.Updated, settings.current.value.listSort)
        assertEquals(EntryOrder.Updated, userList.requests.last().order)
        assertEquals(
            ListSort.Updated,
            viewModel.state.value.sortOptions.single { it.isSelected }.sort,
        )
    }

    @Test
    fun `reversing the direction is saved, reloads, and a new sort starts in its natural direction`() =
        runTest {
            sessions.current.value = SessionSamples.session()
            val viewModel = createViewModel()
            collect(viewModel.state)
            advanceUntilIdle()
            assertTrue(viewModel.state.value.isSortAscending)

            viewModel.onIntent(ProfileIntent.SortDirectionToggled)
            advanceUntilIdle()
            assertTrue(settings.current.value.isListSortReversed)
            assertFalse(viewModel.state.value.isSortAscending)
            assertEquals(false, userList.requests.last().ascending)

            viewModel.onIntent(ProfileIntent.SortSelected(ListSort.Vote))
            advanceUntilIdle()
            assertFalse(settings.current.value.isListSortReversed)
            assertFalse(viewModel.state.value.isSortAscending)
            assertEquals(
                EntryOrder.Vote,
                userList.requests.first { it.order == EntryOrder.Vote }.order,
            )
        }

    @Test
    fun `the edit button opens the list editor with the entry's title`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.canEditList)

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.EntryEditClicked("v1"))
            val effect = awaitItem() as ProfileEffect.OpenListEditor
            assertEquals("v1", effect.novelId)
        }
    }

    @Test
    fun `an entry edited in the app reloads the labels and the list`() = runTest {
        sessions.current.value = SessionSamples.session()
        val viewModel = createViewModel()
        collect(viewModel.state)
        advanceUntilIdle()
        userList.calls.clear()

        userList.saveEntry(
            MyListEntry(novelId = "v1", labelIds = setOf(ListLabel.FINISHED)),
            previous = null,
        )
        advanceUntilIdle()

        assertTrue(userList.calls.containsAll(listOf("labels", "entries:1:1")))
    }
}
