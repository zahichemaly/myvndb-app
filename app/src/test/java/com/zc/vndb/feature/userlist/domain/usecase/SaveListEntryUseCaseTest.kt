package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.util.FixedClock
import com.zc.vndb.feature.userlist.domain.model.InvalidListEntryException
import com.zc.vndb.feature.userlist.domain.model.ListEntryProblem
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate

class SaveListEntryUseCaseTest {

    private val repository = FakeUserListRepository()
    private val save = SaveListEntryUseCase(
        repository,
        FixedClock(Instant.parse("2026-11-16T12:00:00Z")),
    )

    @Test
    fun `keeps one status, drops virtual labels, keeps the vote in range and trims notes`() =
        runTest {
            save(
                MyListEntry(
                    novelId = "v17",
                    labelIds = setOf(
                        ListLabel.PLAYING,
                        ListLabel.FINISHED,
                        ListLabel.VOTED,
                        ListLabel.WISHLIST,
                        12,
                        0,
                        1601,
                    ),
                    vote = 120,
                    notes = "  hi  ",
                ),
                previous = null,
            )

            val saved = repository.saved.single()
            assertEquals(1, saved.labelIds.count { it in MyListEntry.STATUS_LABELS })
            assertEquals(setOf(ListLabel.WISHLIST, 12), saved.labelIds - MyListEntry.STATUS_LABELS)
            assertEquals(MyListEntry.MAX_VOTE, saved.vote)
            assertEquals("hi", saved.notes)
        }

    @Test
    fun `blank notes are cleared`() = runTest {
        save(MyListEntry(novelId = "v17", notes = "   "), previous = null)

        assertEquals(null, repository.saved.single().notes)
    }

    @Test
    fun `an entry VNDB would reject is not sent`() = runTest {
        val previous = MyListEntry(novelId = "v17", started = LocalDate(2026, 1, 1))
        val result = save(
            previous.copy(
                started = null,
                finished = LocalDate(1969, 12, 31),
                notes = "x".repeat(MyListEntry.NOTES_MAX_LENGTH + 1),
            ),
            previous,
        )

        val problems = (result.exceptionOrNull() as InvalidListEntryException).problems
        assertEquals(
            setOf(
                ListEntryProblem.StartedCleared,
                ListEntryProblem.FinishedOutOfRange,
                ListEntryProblem.NotesTooLong,
            ),
            problems,
        )
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun `future dates and a finish before the start are rejected`() = runTest {
        val result = save(
            MyListEntry(
                novelId = "v17",
                started = LocalDate(2026, 11, 10),
                finished = LocalDate(2026, 11, 17),
            ),
            previous = null,
        )
        assertEquals(
            setOf(ListEntryProblem.FinishedOutOfRange),
            (result.exceptionOrNull() as InvalidListEntryException).problems,
        )

        val reversed = save(
            MyListEntry(
                novelId = "v17",
                started = LocalDate(2026, 11, 10),
                finished = LocalDate(2026, 11, 1),
            ),
            previous = null,
        )
        assertEquals(
            setOf(ListEntryProblem.FinishedBeforeStarted),
            (reversed.exceptionOrNull() as InvalidListEntryException).problems,
        )
    }
}
