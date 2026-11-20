package com.zc.vndb.feature.detail.presentation.mapper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LanguageFlagUIModelMapperTest {

    @Test
    fun `languages map to their country's flag, English to the British one`() {
        assertEquals("🇯🇵", languageFlag("ja"))
        assertEquals("🇬🇧", languageFlag("en"))
        assertEquals("🇧🇷", languageFlag("pt-br"))
    }

    @Test
    fun `codes match regardless of case`() {
        assertEquals(languageFlag("zh-hant"), languageFlag("zh-Hant"))
        assertEquals("🇹🇼", languageFlag("zh-Hant"))
    }

    @Test
    fun `languages without a national flag have none`() {
        assertNull(languageFlag("ca"))
        assertNull(languageFlag("eo"))
        assertNull(languageFlag(""))
    }
}
