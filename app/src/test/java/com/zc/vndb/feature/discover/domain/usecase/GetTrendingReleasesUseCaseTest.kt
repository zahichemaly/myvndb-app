package com.zc.vndb.feature.discover.domain.usecase

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples.date
import com.zc.vndb.feature.discover.domain.model.DiscoverSamples.release
import com.zc.vndb.feature.discover.domain.model.ReleaseKind
import com.zc.vndb.feature.discover.domain.model.ReleaseLanguage
import com.zc.vndb.feature.discover.domain.repository.FakeDiscoverRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetTrendingReleasesUseCaseTest {

    private val repository = FakeDiscoverRepository()
    private val useCase = GetTrendingReleasesUseCase(repository)

    private val english = ReleaseLanguage("en", isMachineTranslated = false)
    private val japanese = ReleaseLanguage("ja", isMachineTranslated = false)
    private val englishMtl = ReleaseLanguage("en", isMachineTranslated = true)

    @Test
    fun `asks for English and Japanese releases, over-fetching for de-duplication`() = runTest {
        useCase(LANGUAGES)

        assertEquals(listOf("en", "ja"), repository.requestedReleaseLanguages)
        assertEquals(GetTrendingReleasesUseCase.FETCH_LIMIT, repository.requestedReleaseLimit)
    }

    @Test
    fun `labels each release by why it is new`() = runTest {
        repository.releases = listOf(
            release("r1", novelId = "v1", isPatch = true, novelReleased = date(2020, 1, 1)),
            release(
                "r2",
                novelId = "v2",
                novelReleased = date(2024, 10, 1),
                released = date(2024, 10, 1),
            ),
            release(
                "r3",
                novelId = "v3",
                novelReleased = date(2020, 1, 1),
                languages = listOf(english),
            ),
            release(
                "r4",
                novelId = "v4",
                novelReleased = date(2020, 1, 1),
                languages = listOf(japanese),
            ),
        )

        assertEquals(
            listOf(
                ReleaseKind.RecentPatch,
                ReleaseKind.NewRelease,
                ReleaseKind.NewTranslation,
                ReleaseKind.ReRelease,
            ),
            useCase(LANGUAGES).getOrThrow().map { it.kind },
        )
    }

    @Test
    fun `a new translation names its language, preferring the user's`() = runTest {
        val chinese = ReleaseLanguage("zh-Hans", isMachineTranslated = false)
        repository.releases = listOf(
            release(
                "r1",
                novelId = "v1",
                novelReleased = date(2020, 1, 1),
                languages = listOf(chinese, english),
            ),
            release(
                "r2",
                novelId = "v2",
                novelReleased = date(2020, 1, 1),
                languages = listOf(chinese, englishMtl),
            ),
            release("r3", novelId = "v3", isPatch = true, languages = listOf(english)),
        )

        assertEquals(
            listOf("en", "zh-Hans", null),
            useCase(LANGUAGES).getOrThrow().map { it.translationLanguage },
        )
    }

    @Test
    fun `machine translation does not count as a new translation`() = runTest {
        repository.releases = listOf(
            release(
                "r1",
                novelReleased = date(2020, 1, 1),
                languages = listOf(japanese, englishMtl),
            ),
        )

        assertEquals(ReleaseKind.ReRelease, useCase(LANGUAGES).getOrThrow().single().kind)
    }

    @Test
    fun `drops releases that are machine translated only`() = runTest {
        repository.releases = listOf(
            release("r1", novelId = "v1", languages = listOf(englishMtl)),
            release("r2", novelId = "v2", languages = listOf(english)),
        )

        assertEquals(listOf("r2"), useCase(LANGUAGES).getOrThrow().map { it.releaseId })
    }

    @Test
    fun `keeps only the newest release of each novel`() = runTest {
        repository.releases = listOf(
            release("r-new", novelId = "v1", released = date(2024, 10, 2)),
            release("r-other", novelId = "v2", released = date(2024, 10, 1)),
            release("r-old", novelId = "v1", released = date(2024, 9, 1)),
        )

        assertEquals(
            listOf("r-new", "r-other"),
            useCase(LANGUAGES).getOrThrow().map { it.releaseId },
        )
    }

    @Test
    fun `returns at most RESULT_LIMIT rows`() = runTest {
        repository.releases = (1..20).map { release("r$it", novelId = "v$it") }

        assertEquals(GetTrendingReleasesUseCase.RESULT_LIMIT, useCase(LANGUAGES).getOrThrow().size)
    }

    @Test
    fun `describes the row through its novel`() = runTest {
        repository.releases = listOf(release("r1", novelId = "v9"))

        val row = useCase(LANGUAGES).getOrThrow().single()

        assertEquals("v9", row.novelId)
        assertEquals("Novel v9", row.title)
        assertEquals(81.2, row.rating)
    }

    @Test
    fun `propagates repository errors`() = runTest {
        repository.releasesError = DomainError.Network

        assertEquals(DomainError.Network, useCase(LANGUAGES).exceptionOrNull())
    }

    private companion object {
        val LANGUAGES = listOf("en", "ja")
    }
}
