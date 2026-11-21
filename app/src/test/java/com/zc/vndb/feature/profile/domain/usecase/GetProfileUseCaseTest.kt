package com.zc.vndb.feature.profile.domain.usecase

import com.zc.vndb.common.domain.model.DomainError
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.feature.profile.domain.model.UserProfile
import com.zc.vndb.feature.profile.domain.repository.FakeProfileRepository
import com.zc.vndb.feature.userlist.domain.model.UserListSamples.label
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import com.zc.vndb.feature.userlist.domain.usecase.GetListLabelsUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class GetProfileUseCaseTest {

    private val repository = FakeProfileRepository()
    private val userList = FakeUserListRepository()
    private val useCase = GetProfileUseCase(repository, GetListLabelsUseCase(userList))

    @Test
    fun `keeps non-empty labels, real ones first, then Voted and No label`() = runTest {
        repository.user = UserProfile("u2", "Yorhel", lengthVotes = 10, lengthVotesMinutes = 10239)
        userList.labels = listOf(
            label(ListLabel.NO_LABEL),
            label(13),
            label(ListLabel.VOTED),
            label(ListLabel.WISHLIST),
            label(ListLabel.BLACKLIST, count = 0),
            label(ListLabel.PLAYING),
        )

        val overview = useCase("u2").getOrThrow()

        assertEquals(listOf(1, 5, 13, 7, 0), overview.labels.map { it.id })
        assertEquals(10, overview.user?.lengthVotes)
    }

    @Test
    fun `a failed user lookup still shows the lists`() = runTest {
        userList.labels = listOf(label(ListLabel.PLAYING))

        val overview = useCase("u2").getOrThrow()

        assertNull(overview.user)
        assertEquals(1, overview.labels.size)
    }

    @Test
    fun `failed labels fail the profile`() = runTest {
        userList.labelsError = DomainError.Network

        assertEquals(DomainError.Network, useCase("u2").exceptionOrNull())
    }
}
