package com.zc.vndb.common.domain.model

import com.zc.vndb.common.domain.model.SensitiveContentPolicy.Censored
import com.zc.vndb.common.domain.model.SensitiveContentPolicy.Standard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SensitiveContentPolicyTest {

    @Test
    fun `standard counts suggestive or violent averages and above`() {
        assertFalse(Standard.isSensitive(sexual = 0.99, violence = 0.99))
        assertTrue(Standard.isSensitive(sexual = 1.0, violence = 0.0))
        assertTrue(Standard.isSensitive(sexual = 0.0, violence = 1.0))
    }

    @Test
    fun `censored also counts anything vndb does not rate sexually safe`() {
        assertFalse(Censored.isSensitive(sexual = 0.4, violence = 0.99))
        assertTrue(Censored.isSensitive(sexual = 0.41, violence = 0.0))
        assertTrue(Censored.isSensitive(sexual = 1.0, violence = 0.0))
        assertTrue(Censored.isSensitive(sexual = 0.0, violence = 1.0))
    }

    @Test
    fun `each flavor's config picks its policy`() {
        assertEquals(Standard, AppConfigSamples.development.sensitiveContentPolicy)
        assertEquals(Censored, AppConfigSamples.production.sensitiveContentPolicy)
    }
}
