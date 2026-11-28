package com.zc.vndb.common.data.mapper

import com.zc.vndb.common.data.dto.ImageDTO
import com.zc.vndb.common.domain.model.SensitiveContentPolicy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ImageDTOMapperTest {

    @Test
    fun `missing flags count as safe and no url is no cover`() {
        val cover = coverOf(
            "u",
            null,
            sexual = null,
            violence = null,
            SensitiveContentPolicy.Censored,
        )

        assertEquals(0.0, cover.sexual)
        assertEquals(0.0, cover.violence)
        assertFalse(cover.isSensitive)
        assertNull(
            ImageDTO(
                url = null,
                thumbnail = null,
                sexual = 2.0,
                violence = 0.0,
            ).toCover(SensitiveContentPolicy.Standard),
        )
    }

    @Test
    fun `covers are judged by the policy they are given`() {
        val image = ImageDTO(url = "u", thumbnail = "t", sexual = 0.8, violence = 0.0)

        assertFalse(image.toCover(SensitiveContentPolicy.Standard)?.isSensitive ?: true)
        assertTrue(image.toCover(SensitiveContentPolicy.Censored)?.isSensitive ?: false)
    }
}
