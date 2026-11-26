package com.zc.vndb.feature.discover.domain.usecase

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples
import com.zc.vndb.feature.discover.domain.model.ReleaseLanguage
import com.zc.vndb.feature.discover.domain.model.ReleasesPage
import com.zc.vndb.feature.discover.domain.repository.FakeDiscoverRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetTrendingReleasesPageUseCaseTest {

    private val repository = FakeDiscoverRepository()
    private val useCase = GetTrendingReleasesPageUseCase(repository)
    private val machineTranslated = listOf(ReleaseLanguage("en", isMachineTranslated = true))

    @Test
    fun `a page keeps one row per novel and leaves out machine translations`() = runTest {
        repository.releasePages[1] = ReleasesPage(
            releases = listOf(
                DiscoverSamples.release("r1", novelId = "v1"),
                DiscoverSamples.release("r2", novelId = "v1"),
                DiscoverSamples.release("r3", novelId = "v2", languages = machineTranslated),
            ),
            hasMore = true,
        )

        val page = useCase(listOf("en"), page = 1, shownNovelIds = emptySet()).getOrThrow()

        assertEquals(listOf("r1"), page.rows.map { it.releaseId })
        assertTrue(page.hasMore)
    }

    @Test
    fun `novels shown on earlier pages are left out`() = runTest {
        repository.releasePages[2] = ReleasesPage(
            releases = listOf(
                DiscoverSamples.release("r4", novelId = "v1"),
                DiscoverSamples.release("r5", novelId = "v3"),
            ),
            hasMore = false,
        )

        val page = useCase(listOf("en"), page = 2, shownNovelIds = setOf("v1")).getOrThrow()

        assertEquals(listOf("v3"), page.rows.map { it.novelId })
        assertFalse(page.hasMore)
    }

    @Test
    fun `asks for the requested page in the user's languages`() = runTest {
        useCase(listOf("en", "ja"), page = 3, shownNovelIds = emptySet())

        assertEquals(listOf(3), repository.requestedReleasePages)
        assertEquals(listOf("en", "ja"), repository.requestedReleaseLanguages)
        assertEquals(GetTrendingReleasesPageUseCase.PAGE_SIZE, repository.requestedReleaseLimit)
    }

    @Test
    fun `no language is an empty last page, without asking VNDB`() = runTest {
        val page = useCase(emptyList(), page = 1, shownNovelIds = emptySet()).getOrThrow()

        assertTrue(page.rows.isEmpty())
        assertFalse(page.hasMore)
        assertTrue(repository.requestedReleasePages.isEmpty())
    }

    @Test
    fun `propagates repository errors`() = runTest {
        repository.failingReleasePages += 1

        val error = useCase(listOf("en"), page = 1, shownNovelIds = emptySet()).exceptionOrNull()

        assertEquals(DomainError.Network, error)
    }
}
