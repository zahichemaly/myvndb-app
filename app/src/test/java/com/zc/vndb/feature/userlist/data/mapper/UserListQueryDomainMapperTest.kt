package com.zc.vndb.feature.userlist.data.mapper

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.userlist.domain.model.ListFilter
import kotlin.test.Test
import kotlin.test.assertEquals

class UserListQueryDomainMapperTest {

    private fun filtersOf(filter: ListFilter, voted: Boolean? = null): String =
        entriesQuery("u1", filter, page = 1, pageSize = 25, voted = voted).filters.toString()

    @Test
    fun `one label is a single predicate`() {
        assertEquals("[\"label\",\"=\",1]", filtersOf(ListFilter(setOf(ListLabel.PLAYING))))
    }

    @Test
    fun `no label and no search ask for the whole list`() {
        assertEquals("[]", filtersOf(ListFilter()))
        assertEquals("[]", filtersOf(ListFilter(search = "   ")))
    }

    @Test
    fun `any of the statuses and any of the user labels, with the vote phase and the search`() {
        val filter = ListFilter(
            labelIds = setOf(ListLabel.STALLED, ListLabel.PLAYING, 12, 10),
            search = " umineko ",
        )

        assertEquals(
            "[\"and\"," +
                "[\"or\",[\"label\",\"=\",1],[\"label\",\"=\",3]]," +
                "[\"or\",[\"label\",\"=\",10],[\"label\",\"=\",12]]," +
                "[\"label\",\"!=\",7]," +
                "[\"search\",\"=\",\"umineko\"]]",
            filtersOf(filter, voted = false),
        )
    }

    @Test
    fun `a search alone searches the whole list`() {
        assertEquals("[\"search\",\"=\",\"rance\"]", filtersOf(ListFilter(search = "rance")))
    }
}
