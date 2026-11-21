package com.zc.vndb.feature.profile.domain.usecase

import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.feature.profile.domain.model.EntryBatch
import com.zc.vndb.feature.profile.domain.model.EntryOrder
import com.zc.vndb.feature.profile.domain.model.ListCursor
import com.zc.vndb.feature.profile.domain.model.ListFilter
import com.zc.vndb.feature.profile.domain.model.ListPhase
import com.zc.vndb.feature.profile.domain.repository.ProfileRepository
import org.koin.core.annotation.Factory

/**
 * Entries matching a [ListFilter] in [ListSort] order, a page at a time. Natural directions: title A to Z,
 * most recently updated first, highest vote first; `reversed` flips them.
 *
 * Sorting by vote pages through the voted entries first and then the unvoted ones (by title, A to
 * Z, in either direction), so entries without a vote always come last; the last voted page is
 * topped up with the first unvoted one.
 */
@Factory
class GetListEntriesUseCase(private val repository: ProfileRepository) {

    suspend operator fun invoke(
        userId: String,
        filter: ListFilter,
        sort: ListSort,
        reversed: Boolean = false,
        cursor: ListCursor? = null,
    ): Result<EntryBatch> {
        val start = cursor ?: firstCursor(filter, sort)
        val order = orderFor(sort, start.phase)
        val page = repository.getEntries(
            userId = userId,
            filter = filter,
            page = start.page,
            pageSize = PAGE_SIZE,
            order = order,
            ascending = if (start.phase ==
                ListPhase.Unvoted
            ) {
                true
            } else {
                order.isNaturallyAscending != reversed
            },
            voted = when (start.phase) {
                ListPhase.All -> null
                ListPhase.Voted -> true
                ListPhase.Unvoted -> false
            },
        ).getOrElse { return Result.failure(it) }
        return when {
            page.hasMore -> Result.success(
                EntryBatch(page.entries, start.copy(page = start.page + 1)),
            )

            start.phase != ListPhase.Voted -> Result.success(EntryBatch(page.entries, next = null))

            // Last voted page: top it up with the first unvoted one.
            else -> invoke(
                userId,
                filter,
                sort,
                reversed,
                ListCursor(FIRST_PAGE, ListPhase.Unvoted),
            )
                .map { unvoted -> EntryBatch(page.entries + unvoted.entries, unvoted.next) }
        }
    }

    private fun firstCursor(filter: ListFilter, sort: ListSort): ListCursor = ListCursor(
        page = FIRST_PAGE,
        // Every entry of the Voted label has a vote, so it needs no unvoted phase.
        phase = if (sort == ListSort.Vote && !filter.isVotedOnly) {
            ListPhase.Voted
        } else {
            ListPhase.All
        },
    )

    private fun orderFor(sort: ListSort, phase: ListPhase): EntryOrder = when (phase) {
        ListPhase.Voted -> EntryOrder.Vote

        ListPhase.Unvoted -> EntryOrder.Title

        ListPhase.All -> when (sort) {
            ListSort.Title -> EntryOrder.Title
            ListSort.Updated -> EntryOrder.Updated
            ListSort.Vote -> EntryOrder.Vote
        }
    }

    /** Title runs A to Z; updates and votes run newest / highest first. */
    private val EntryOrder.isNaturallyAscending: Boolean get() = this == EntryOrder.Title

    companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 25
    }
}
