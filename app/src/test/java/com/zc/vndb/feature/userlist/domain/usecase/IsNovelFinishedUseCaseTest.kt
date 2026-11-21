package com.zc.vndb.feature.userlist.domain.usecase

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.domain.model.Session
import com.zc.vndb.common.domain.repository.FakeSessionRepository
import com.zc.vndb.feature.userlist.domain.repository.FakeUserListRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class IsNovelFinishedUseCaseTest {

    private val sessions = FakeSessionRepository()
    private val repository = FakeUserListRepository()
    private val useCase = IsNovelFinishedUseCase(sessions, repository)

    @Test
    fun `a guest has nothing finished and nothing is requested`() = runTest {
        assertFalse(useCase("v1"))
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun `true only when the entry has the Finished label`() = runTest {
        sessions.current.value = Session("u2", "Yorhel", canReadPrivateList = true)
        repository.entryLabels["v1"] = listOf(ListLabel.FINISHED, ListLabel.VOTED)
        repository.entryLabels["v2"] = listOf(ListLabel.PLAYING)

        assertTrue(useCase("v1"))
        assertFalse(useCase("v2"))
        assertFalse(useCase("v3"))
    }
}
