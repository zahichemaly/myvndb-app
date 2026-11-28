package com.zc.vndb.common.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoverTest {

    private fun cover(sexual: Double, policy: SensitiveContentPolicy) =
        Cover(url = "u", thumbnailUrl = null, sexual = sexual, violence = 0.0, policy = policy)

    @Test
    fun `a cover is judged by its build's policy`() {
        assertFalse(cover(sexual = 0.8, policy = SensitiveContentPolicy.Standard).isSensitive)
        assertTrue(cover(sexual = 0.8, policy = SensitiveContentPolicy.Censored).isSensitive)
    }
}
