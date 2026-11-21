package com.zc.vndb.feature.profile.domain.usecase

import com.zc.vndb.common.domain.model.ListSort
import com.zc.vndb.feature.profile.domain.model.EntryOrder
import com.zc.vndb.feature.profile.domain.model.ListFilter
import com.zc.vndb.feature.profile.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.ListPhase
import com.zc.vndb.feature.profile.domain.model.ProfileSamples
import com.zc.vndb.feature.profile.domain.repository.FakeProfileRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class GetListEntriesUseCaseTest {

    private val repository = FakeProfileRepository()
    private val useCase = GetListEntriesUseCase(repository)

    @Test
    fun `title and updated sorts are one ordered list`() = runTest {
        repository.entries[ListLabel.PLAYING] = listOf(
            ProfileSamples.entry("v1").copy(title = "Clannad"),
            ProfileSamples.entry("v2").copy(title = "Air"),
        )

        val byTitle = useCase("u2", PLAYING, ListSort.Title).getOrThrow()

        assertEquals(listOf("Air", "Clannad"), byTitle.entries.map { it.title })
        assertNull(byTitle.next)
        assertEquals(EntryOrder.Title, repository.requests.single().order)
        assertEquals(true, repository.requests.single().ascending)

        useCase("u2", PLAYING, ListSort.Updated)
        assertEquals(EntryOrder.Updated, repository.requests.last().order)
        assertEquals(false, repository.requests.last().ascending)
    }

    @Test
    fun `vote sort lists voted entries by vote, then unvoted ones by title`() = runTest {
        repository.entries[ListLabel.FINISHED] = listOf(
            ProfileSamples.entry("v1", vote = 70).copy(title = "B"),
            ProfileSamples.entry("v2").copy(title = "Z"),
            ProfileSamples.entry("v3", vote = 90).copy(title = "C"),
            ProfileSamples.entry("v4").copy(title = "A"),
        )

        val batch = useCase("u2", ListFilter(setOf(ListLabel.FINISHED)), ListSort.Vote).getOrThrow()

        assertEquals(listOf("v3", "v1", "v4", "v2"), batch.entries.map { it.novelId })
        assertEquals(listOf(true, false), repository.requests.map { it.voted })
        assertNull(batch.next)
    }

    @Test
    fun `vote sort pages through voted entries before the unvoted ones`() = runTest {
        repository.entries[ListLabel.FINISHED] =
            (1..30).map { ProfileSamples.entry("v$it", vote = 100 - it) } +
            (31..33).map { ProfileSamples.entry("v$it") }

        val first = useCase("u2", ListFilter(setOf(ListLabel.FINISHED)), ListSort.Vote).getOrThrow()
        assertEquals(25, first.entries.size)
        assertEquals(ListPhase.Voted, first.next?.phase)

        val second = useCase(
            "u2",
            ListFilter(setOf(ListLabel.FINISHED)),
            ListSort.Vote,
            cursor = first.next,
        ).getOrThrow()
        assertEquals((26..33).map { "v$it" }, second.entries.map { it.novelId })
        assertNull(second.next)
    }

    @Test
    fun `the Voted label needs no unvoted phase`() = runTest {
        repository.entries[ListLabel.VOTED] = listOf(ProfileSamples.entry("v1", vote = 80))

        useCase("u2", ListFilter(setOf(ListLabel.VOTED)), ListSort.Vote).getOrThrow()

        assertEquals(listOf<Boolean?>(null), repository.requests.map { it.voted })
        assertEquals(EntryOrder.Vote, repository.requests.single().order)
    }

    @Test
    fun `reversed flips title and updated`() = runTest {
        repository.entries[ListLabel.PLAYING] = listOf(
            ProfileSamples.entry("v1").copy(title = "Air"),
            ProfileSamples.entry("v2").copy(title = "Clannad"),
        )

        val zToA = useCase("u2", PLAYING, ListSort.Title, reversed = true).getOrThrow()
        assertEquals(listOf("Clannad", "Air"), zToA.entries.map { it.title })

        useCase("u2", PLAYING, ListSort.Updated, reversed = true)
        assertEquals(true, repository.requests.last().ascending)
    }

    @Test
    fun `reversed vote sort is lowest vote first and still keeps unvoted entries last, A to Z`() =
        runTest {
            repository.entries[ListLabel.FINISHED] = listOf(
                ProfileSamples.entry("v1", vote = 70).copy(title = "B"),
                ProfileSamples.entry("v2").copy(title = "Z"),
                ProfileSamples.entry("v3", vote = 90).copy(title = "C"),
                ProfileSamples.entry("v4").copy(title = "A"),
            )

            val batch = useCase(
                "u2",
                ListFilter(setOf(ListLabel.FINISHED)),
                ListSort.Vote,
                reversed = true,
            ).getOrThrow()

            assertEquals(listOf("v1", "v3", "v4", "v2"), batch.entries.map { it.novelId })
            assertEquals(listOf(true, true), repository.requests.map { it.ascending })
        }

    private companion object {
        val PLAYING = ListFilter(setOf(ListLabel.PLAYING))
    }
}
