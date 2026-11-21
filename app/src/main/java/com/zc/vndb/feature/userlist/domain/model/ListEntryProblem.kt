package com.zc.vndb.feature.userlist.domain.model

/** Why VNDB would reject a list entry (rules of `PATCH /ulist/<id>`). */
enum class ListEntryProblem {
    /** Notes are longer than [MyListEntry.NOTES_MAX_LENGTH]. */
    NotesTooLong,

    /** The start date is before [MyListEntry.MIN_DATE] or after today. */
    StartedOutOfRange,

    /** The finish date is before [MyListEntry.MIN_DATE] or after today. */
    FinishedOutOfRange,

    FinishedBeforeStarted,

    /** A saved start date was removed: VNDB can change a date but not clear it. */
    StartedCleared,

    /** A saved finish date was removed. */
    FinishedCleared,
}
