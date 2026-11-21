package com.zc.vndb.feature.userlist.domain.model

import com.zc.vndb.common.domain.model.ListLabel

/**
 * Which entries a Profile list shows. An entry matches when it has any of the selected statuses
 * and any of the selected user labels (a group with nothing selected does not filter), and its
 * titles match [search].
 */
data class ListFilter(
    /** Selected label ids: VNDB's own ones (statuses, Voted, No label) and the user's. Empty: all. */
    val labelIds: Set<Int> = emptySet(),
    /** Matched against the VN's titles, aliases and release titles; blank matches everything. */
    val search: String = "",
) {
    /** VNDB's own labels: ids below [ListLabel.FIRST_USER_ID]. */
    val statusIds: Set<Int>
        get() = labelIds.filterTo(sortedSetOf()) { it < ListLabel.FIRST_USER_ID }

    /** Labels the user created. */
    val userLabelIds: Set<Int>
        get() = labelIds.filterTo(sortedSetOf()) { it >= ListLabel.FIRST_USER_ID }

    /** Every matching entry has a vote: only the Voted status is selected. */
    val isVotedOnly: Boolean get() = statusIds == setOf(ListLabel.VOTED)
}
