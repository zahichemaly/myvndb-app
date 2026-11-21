package com.zc.vndb.feature.userlist.domain.model

import com.zc.vndb.common.domain.model.ListLabel
import kotlinx.datetime.LocalDate

/**
 * The signed-in user's own entry for one visual novel: what the list editor shows and saves.
 * [labelIds] never holds the virtual labels ([ListLabel.NO_LABEL], [ListLabel.VOTED]).
 */
data class MyListEntry(
    val novelId: String,
    val labelIds: Set<Int> = emptySet(),
    /** [MIN_VOTE]–[MAX_VOTE] (1.0–10.0 on screen), or null without a vote. */
    val vote: Int? = null,
    val started: LocalDate? = null,
    val finished: LocalDate? = null,
    val notes: String? = null,
    /** Names of the labels in [labelIds], as VNDB sent them. Read-only: not saved. */
    val labelNames: Map<Int, String> = emptyMap(),
) {
    /** Playing, Finished, Stalled or Dropped: at most one of them, as on the website. */
    val status: Int? get() = labelIds.firstOrNull { it in STATUS_LABELS }

    /**
     * What VNDB would reject, given the entry as it was loaded ([previous], null when it was not on
     * the list) and [today]. Empty when the entry can be saved.
     */
    fun problems(previous: MyListEntry?, today: LocalDate): Set<ListEntryProblem> = buildSet {
        if ((notes?.length ?: 0) > NOTES_MAX_LENGTH) add(ListEntryProblem.NotesTooLong)
        if (started != null && started !in MIN_DATE..today) add(ListEntryProblem.StartedOutOfRange)
        if (finished != null && finished !in MIN_DATE..today) {
            add(
                ListEntryProblem.FinishedOutOfRange,
            )
        }
        if (started != null && finished != null && finished < started) {
            add(ListEntryProblem.FinishedBeforeStarted)
        }
        if (started == null && previous?.started != null) add(ListEntryProblem.StartedCleared)
        if (finished == null && previous?.finished != null) add(ListEntryProblem.FinishedCleared)
    }

    companion object {
        const val MIN_VOTE = 10
        const val MAX_VOTE = 100
        const val NOTES_MAX_LENGTH = 2000

        /** VNDB takes dates from 1970 on (`YYYY-MM-DD`, years 1970–2099). */
        val MIN_DATE = LocalDate(1970, 1, 1)

        /** Label ids VNDB accepts on an entry. */
        val LABEL_IDS = 1..1600

        /** The labels that are a reading status; the website keeps one at a time. */
        val STATUS_LABELS = setOf(
            ListLabel.PLAYING,
            ListLabel.FINISHED,
            ListLabel.STALLED,
            ListLabel.DROPPED,
        )

        /** Labels VNDB manages itself; they can't be set or unset. */
        val VIRTUAL_LABELS = setOf(ListLabel.NO_LABEL, ListLabel.VOTED)
    }
}
