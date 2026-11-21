package com.zc.vndb.feature.detail.presentation

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.detail.presentation.model.SectionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class SectionLoaderTest {

    private var calls = 0
    private val results = ArrayDeque<Result<String>>()
    private val changes = mutableListOf<SectionState<String>>()

    private fun TestScope.loader(): SectionLoader<String> {
        lateinit var loader: SectionLoader<String>
        loader = SectionLoader(
            scope = this,
            fetch = {
                calls++
                results.removeFirst()
            },
        ) { changes += loader.state }
        return loader
    }

    @Test
    fun `loads once, keeps its data and reports each change`() = runTest {
        results += Result.success("editions")
        val loader = loader()

        loader.loadOnce()
        loader.loadOnce()
        advanceUntilIdle()
        loader.loadOnce()

        assertEquals(1, calls)
        assertEquals("editions", loader.data)
        assertEquals(listOf(SectionState.Loading, SectionState.Loaded("editions")), changes)
    }

    @Test
    fun `a failed load waits for a retry`() = runTest {
        results += Result.failure(DomainError.Network)
        results += Result.success("cast")
        val loader = loader()

        loader.loadOnce()
        advanceUntilIdle()
        assertIs<SectionState.Failed>(loader.state)
        assertNull(loader.data)

        loader.loadOnce()
        advanceUntilIdle()
        assertEquals(1, calls)

        loader.load()
        advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals("cast", loader.data)
    }

    @Test
    fun `a retry while loading does not start a second request`() = runTest {
        results += Result.success("covers")
        val loader = loader()

        loader.load()
        loader.load()
        advanceUntilIdle()

        assertEquals(1, calls)
    }
}
