package com.zc.vndb.feature.userlist.presentation

import app.cash.turbine.test
import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.model.SessionSamples
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.common.domain.util.FixedClock
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.util.MainDispatcherRule
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.model.UserListSamples
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import com.zc.vndb.feature.userlist.domain.usecase.GetEditableLabelsUseCase
import com.zc.vndb.feature.userlist.domain.usecase.GetMyListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.RemoveListEntryUseCase
import com.zc.vndb.feature.userlist.domain.usecase.SaveListEntryUseCase
import com.zc.vndb.feature.userlist.presentation.model.DateField
import com.zc.vndb.feature.userlist.presentation.model.ListEditorArgs
import com.zc.vndb.feature.userlist.presentation.model.ListEditorDialog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Rule

class ListEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sessions = FakeSessionRepository().apply {
        current.value = SessionSamples.session()
    }
    private val repository = FakeUserListRepository().apply {
        labels = listOf(
            UserListSamples.label(ListLabel.NO_LABEL, name = "No label"),
            UserListSamples.label(ListLabel.PLAYING, name = "Playing"),
            UserListSamples.label(ListLabel.FINISHED, name = "Finished"),
            UserListSamples.label(ListLabel.WISHLIST, name = "Wishlist"),
            UserListSamples.label(ListLabel.VOTED, name = "Voted"),
            UserListSamples.label(12, name = "Favourites"),
        )
    }

    private val clock = FixedClock(Instant.parse("2026-11-16T12:00:00Z"))

    private fun createViewModel() = ListEditorViewModel(
        args = ListEditorArgs("v17", "Ever17"),
        getMyListEntry = GetMyListEntryUseCase(sessions, repository),
        getEditableLabels = GetEditableLabelsUseCase(sessions, repository),
        saveListEntry = SaveListEntryUseCase(repository, clock),
        removeListEntry = RemoveListEntryUseCase(repository),
        clock = clock,
    )

    @Test
    fun `a VN not on the list starts empty, without virtual labels`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertFalse(isOnList)
            assertEquals(listOf(ListLabel.PLAYING, ListLabel.FINISHED), statuses.map { it.id })
            assertEquals(listOf(ListLabel.WISHLIST, 12), otherLabels.map { it.id })
            assertNull(vote)
            assertEquals(UiText.StringValue("Ever17"), novelTitle)
        }
    }

    @Test
    fun `an existing entry fills the fields`() = runTest {
        repository.myEntries["v17"] = MyListEntry(
            novelId = "v17",
            labelIds = setOf(ListLabel.PLAYING, 12),
            vote = 85,
            started = LocalDate(2026, 11, 1),
            notes = "Hi",
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertTrue(isOnList)
            assertTrue(statuses.single { it.id == ListLabel.PLAYING }.isSelected)
            assertTrue(otherLabels.single { it.id == 12 }.isSelected)
            assertEquals(UiText.StringValue("8.5"), voteLabel)
            assertEquals(UiText.StringValue("2026-11-01"), startedLabel)
            assertEquals("Hi", notes)
        }
    }

    @Test
    fun `statuses are exclusive and tapping the selected one clears it`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ListEditorIntent.StatusSelected(ListLabel.PLAYING))
        viewModel.onIntent(ListEditorIntent.StatusSelected(ListLabel.FINISHED))
        assertEquals(
            listOf(ListLabel.FINISHED),
            viewModel.state.value.statuses.filter { it.isSelected }.map { it.id },
        )

        viewModel.onIntent(ListEditorIntent.StatusSelected(ListLabel.FINISHED))
        assertTrue(viewModel.state.value.statuses.none { it.isSelected })
    }

    @Test
    fun `save writes the whole entry and closes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ListEditorIntent.StatusSelected(ListLabel.FINISHED))
        viewModel.onIntent(ListEditorIntent.LabelToggled(ListLabel.WISHLIST))
        viewModel.onIntent(ListEditorIntent.VoteChanged(77))
        viewModel.onIntent(ListEditorIntent.DateClicked(DateField.Finished))
        assertEquals(
            ListEditorDialog.PickDate(DateField.Finished, initialMillis = null),
            viewModel.state.value.dialog,
        )
        viewModel.onIntent(
            ListEditorIntent.DatePicked(DateField.Finished, millis = 1_793_491_200_000),
        )
        viewModel.onIntent(ListEditorIntent.NotesChanged("Great"))

        viewModel.effect.test {
            viewModel.onIntent(ListEditorIntent.SaveClicked)
            assertEquals(ListEditorEffect.Close, awaitItem())
        }
        assertEquals(
            MyListEntry(
                novelId = "v17",
                labelIds = setOf(ListLabel.FINISHED, ListLabel.WISHLIST),
                vote = 77,
                finished = LocalDate(2026, 11, 1),
                notes = "Great",
            ),
            repository.saved.single(),
        )
    }

    @Test
    fun `a failed save keeps the edits and shows the error`() = runTest {
        repository.writeError = DomainError.Network
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ListEditorIntent.VoteChanged(60))
        viewModel.onIntent(ListEditorIntent.SaveClicked)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isSaving)
            assertNotNull(saveError)
            assertEquals(60, vote)
        }
    }

    @Test
    fun `remove asks first, then removes and closes`() = runTest {
        repository.myEntries["v17"] = MyListEntry(
            novelId = "v17",
            labelIds = setOf(ListLabel.PLAYING),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ListEditorIntent.RemoveClicked)
        assertEquals(ListEditorDialog.ConfirmRemove, viewModel.state.value.dialog)
        assertTrue(repository.removed.isEmpty())

        viewModel.effect.test {
            viewModel.onIntent(ListEditorIntent.RemoveConfirmed)
            assertEquals(ListEditorEffect.Close, awaitItem())
        }
        assertEquals(listOf("v17"), repository.removed)
    }

    @Test
    fun `vote and dates can be cleared`() = runTest {
        repository.myEntries["v17"] = MyListEntry(
            novelId = "v17",
            vote = 50,
            started = LocalDate(2026, 1, 1),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(ListEditorIntent.VoteCleared)
        viewModel.onIntent(ListEditorIntent.DateCleared(DateField.Started))

        assertNull(viewModel.state.value.vote)
        assertNull(viewModel.state.value.startedLabel)
    }

    @Test
    fun `a failed load is retryable`() = runTest {
        repository.myEntryError = DomainError.Network
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)

        repository.myEntryError = null
        viewModel.onIntent(ListEditorIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `only unsaved dates can be cleared and invalid input disables Save`() = runTest {
        repository.myEntries["v17"] = MyListEntry(novelId = "v17", started = LocalDate(2026, 1, 1))
        val viewModel = createViewModel()
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(canClearStarted)
            assertNotNull(datesHint)
            assertTrue(isValid)
        }

        viewModel.onIntent(
            ListEditorIntent.DatePicked(DateField.Finished, millis = 1_767_225_600_000),
        ) // 2026-01-01
        assertTrue(viewModel.state.value.canClearFinished)

        viewModel.onIntent(
            ListEditorIntent.DatePicked(DateField.Finished, millis = 1_764_547_200_000),
        ) // 2025-12-01
        with(viewModel.state.value) {
            assertNotNull(finishedError)
            assertFalse(isValid)
        }

        viewModel.onIntent(ListEditorIntent.DateCleared(DateField.Finished))
        viewModel.onIntent(
            ListEditorIntent.NotesChanged("x".repeat(MyListEntry.NOTES_MAX_LENGTH + 1)),
        )
        with(viewModel.state.value) {
            assertNotNull(notesError)
            assertFalse(isValid)
        }
        viewModel.onIntent(ListEditorIntent.SaveClicked)
        advanceUntilIdle()
        assertTrue(repository.saved.isEmpty())
    }
}
