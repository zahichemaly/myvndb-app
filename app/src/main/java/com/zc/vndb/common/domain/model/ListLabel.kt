package com.zc.vndb.common.domain.model

/** A list category: VNDB's predefined labels (ids below 10) or one the user created. */
data class ListLabel(
    val id: Int,
    val name: String,
    /** Entries with this label. */
    val count: Int,
    val isPrivate: Boolean,
) {
    companion object {
        /** Virtual label for entries without any other label. */
        const val NO_LABEL = 0
        const val PLAYING = 1
        const val FINISHED = 2
        const val STALLED = 3
        const val DROPPED = 4
        const val WISHLIST = 5
        const val BLACKLIST = 6

        /** Virtual label for every VN the user voted on. */
        const val VOTED = 7

        /** Ids from here on are labels the user created. */
        const val FIRST_USER_ID = 10
    }
}
