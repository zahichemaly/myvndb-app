package com.zc.vndb.feature.userlist.domain.model

import com.zc.vndb.common.domain.model.ListLabel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ListFilterTest {

    @Test
    fun `VNDB's own labels are statuses, the user's start at 10`() {
        val filter = ListFilter(
            setOf(12, ListLabel.VOTED, ListLabel.NO_LABEL, 10, ListLabel.PLAYING),
        )

        assertEquals(
            setOf(ListLabel.NO_LABEL, ListLabel.PLAYING, ListLabel.VOTED),
            filter.statusIds,
        )
        assertEquals(setOf(10, 12), filter.userLabelIds)
    }

    @Test
    fun `only the Voted status means every entry has a vote`() {
        assertTrue(ListFilter(setOf(ListLabel.VOTED)).isVotedOnly)
        assertTrue(ListFilter(setOf(ListLabel.VOTED, 10)).isVotedOnly)
        assertFalse(ListFilter(setOf(ListLabel.VOTED, ListLabel.PLAYING)).isVotedOnly)
        assertFalse(ListFilter().isVotedOnly)
    }
}
