package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.detail.domain.model.ResultPage
import com.zc.vndb.feature.detail.presentation.model.SectionState
import com.zc.vndb.feature.detail.presentation.model.dataOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class PagedSectionLoaderTest {

    /** Pages asked for, in order. */
    private val requested = mutableListOf<Int>()

    /** What each page answers; a page missing here fails. */
    private val pages = mutableMapOf<Int, ResultPage<Int>>()

    private fun TestScope.loader() = PagedSectionLoader(
        scope = this,
        fetch = { page ->
            requested += page
            pages[page]?.let { Result.success(it) } ?: Result.failure(DomainError.Network)
        },
        key = { it },
    ) {}

    @Test
    fun `pages are appended without duplicates`() = runTest {
        pages[1] = ResultPage(listOf(1, 2), hasMore = true)
        pages[2] = ResultPage(listOf(2, 3), hasMore = false)
        val loader = loader()

        loader.loadOnce()
        advanceUntilIdle()
        loader.loadMore()
        advanceUntilIdle()

        assertEquals(listOf(1, 2), requested)
        assertEquals(listOf(1, 2, 3), loader.items)
        assertFalse(assertNotNull(loader.state.dataOrNull).hasMore)
    }

    @Test
    fun `a failed next page keeps the items and offers load more again`() = runTest {
        pages[1] = ResultPage(listOf(1, 2), hasMore = true)
        val loader = loader()
        loader.loadOnce()
        advanceUntilIdle()

        loader.loadMore()
        advanceUntilIdle()

        with(assertNotNull(loader.state.dataOrNull)) {
            assertEquals(listOf(1, 2), items)
            assertTrue(loadMoreFailed)
            assertFalse(isLoadingMore)
        }

        pages[2] = ResultPage(listOf(3), hasMore = false)
        loader.loadMore()
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 2), requested)
        assertEquals(listOf(1, 2, 3), loader.items)
    }

    @Test
    fun `load more does nothing before the first page is in`() = runTest {
        val loader = loader()

        loader.loadMore()
        advanceUntilIdle()

        assertEquals(emptyList(), requested)
        assertEquals(SectionState.Idle, loader.state)
    }

    @Test
    fun `a retry starts again from the first page`() = runTest {
        pages[1] = ResultPage(listOf(1), hasMore = true)
        pages[2] = ResultPage(listOf(2), hasMore = true)
        val loader = loader()
        loader.loadOnce()
        advanceUntilIdle()
        loader.loadMore()
        advanceUntilIdle()

        loader.load()
        advanceUntilIdle()
        loader.loadMore()
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 1, 2), requested)
        assertEquals(listOf(1, 2), loader.items)
    }
}
