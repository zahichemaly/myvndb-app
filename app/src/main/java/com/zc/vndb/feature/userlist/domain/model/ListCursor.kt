package com.zc.vndb.feature.userlist.domain.model

/** Where the next page of a list comes from. Sorting by vote pages through voted, then unvoted entries. */
data class ListCursor(val page: Int, val phase: ListPhase)

enum class ListPhase {
    /** Every entry of the label, in one order. */
    All,

    /** Entries the user voted on, highest vote first. */
    Voted,

    /** Entries without a vote, by title (after [Voted]). */
    Unvoted,
}
