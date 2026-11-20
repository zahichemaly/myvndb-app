package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.data.dto.SchemaDTO
import com.zc.vndb.common.data.network.appJson
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
    fun `Bosnian and Estonian have their national flags`() {
        assertEquals("🇧🇦", languageFlag("bs"))
        assertEquals("🇪🇪", languageFlag("et"))
    }

    @Test
    fun `VNDB's Tagalog code ta shows the Philippine flag`() {
        assertEquals("🇵🇭", languageFlag("ta"))
        assertNull(languageFlag("tl"))
    }

    @Test
    fun `languages without a national flag have none`() {
        listOf("ca", "eu", "gl", "gd", "eo", "la", "iu", "ck", "").forEach {
            assertNull(languageFlag(it), "expected no flag for '$it'")
        }
    }

    @Test
    fun `every language VNDB lists has a flag, except the ones without a national flag`() {
        val schema = checkNotNull(
            LanguageFlagUIModelMapperTest::class.java.getResource("/schema/schema.json"),
        ).readText()
        val codes = appJson.decodeFromString<SchemaDTO>(schema).enums.language.map { it.id }
        val withoutFlag = setOf("ca", "eu", "gl", "gd", "eo", "la", "iu", "ck")

        assertEquals(57, codes.size)
        assertEquals(withoutFlag, codes.filter { languageFlag(it) == null }.toSet())
    }
}
