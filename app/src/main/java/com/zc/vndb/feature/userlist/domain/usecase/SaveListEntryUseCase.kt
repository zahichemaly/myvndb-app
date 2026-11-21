package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.feature.userlist.domain.model.InvalidListEntryException
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.domain.repository.UserListRepository
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.annotation.Factory

/**
 * Adds a VN to the signed-in user's list or updates its entry. Labels VNDB refuses (virtual or
 * out-of-range ids) are dropped, only one reading status is kept (VNDB doesn't enforce it), the
 * vote is kept in range and notes are trimmed. An entry that still breaks a rule
 * ([MyListEntry.problems]) fails with [InvalidListEntryException] without a request.
 */
@Factory
class SaveListEntryUseCase(private val repository: UserListRepository, private val clock: Clock) {

    /** [previous] is the entry as loaded (null when the VN was not on the list). */
    suspend operator fun invoke(entry: MyListEntry, previous: MyListEntry?): Result<Unit> {
        val status = entry.status
        val labels = entry.labelIds
            .filter { it in MyListEntry.LABEL_IDS && it !in MyListEntry.VIRTUAL_LABELS }
            .filter { it !in MyListEntry.STATUS_LABELS || it == status }
            .toSet()
        val cleaned = entry.copy(
            labelIds = labels,
            vote = entry.vote?.coerceIn(MyListEntry.MIN_VOTE, MyListEntry.MAX_VOTE),
            notes = entry.notes?.trim()?.takeIf { it.isNotEmpty() },
        )
        val problems = cleaned.problems(previous, today())
        if (problems.isNotEmpty()) return Result.failure(InvalidListEntryException(problems))
        return repository.saveEntry(cleaned, previous)
    }

    private fun today(): LocalDate = clock.todayIn(TimeZone.currentSystemDefault())
}
