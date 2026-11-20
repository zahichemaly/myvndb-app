package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.LanguageOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LanguagePillUIModelMapperTest {

    private val english = LanguageOption("en", "English")
    private val japanese = LanguageOption("ja", "Japanese")
    private val catalan = LanguageOption("ca", "Catalan")

    @Test
    fun `nothing chosen means no pills`() {
        assertTrue(toLanguagePills(listOf("ja", "en"), emptyList()).isEmpty())
    }

    @Test
    fun `a pill for each chosen language the novel is available in`() {
        val pills = toLanguagePills(listOf("ja", "en", "ko"), listOf(english, japanese))

        assertEquals(listOf("en", "ja"), pills.map { it.code })
        assertEquals(listOf("English", "Japanese"), pills.map { it.name })
    }

    @Test
    fun `chosen languages the novel lacks are left out`() {
        val pills = toLanguagePills(listOf("ja"), listOf(english, japanese))

        assertEquals(listOf("ja"), pills.map { it.code })
        assertTrue(toLanguagePills(listOf("ko"), listOf(english)).isEmpty())
        assertTrue(toLanguagePills(emptyList(), listOf(english)).isEmpty())
    }

    @Test
    fun `pills follow the order the user picked`() {
        val pills = toLanguagePills(listOf("en", "ja"), listOf(japanese, english))

        assertEquals(listOf("ja", "en"), pills.map { it.code })
    }

    @Test
    fun `a flag comes with the name, and none for a language without one`() {
        val pills = toLanguagePills(listOf("en", "ca"), listOf(english, catalan))

        assertEquals("🇬🇧", pills[0].flag)
        assertEquals("🇬🇧 English", pills[0].label)
        assertEquals(null, pills[1].flag)
        assertEquals("Catalan", pills[1].label)
    }

    @Test
    fun `without a label the name comes from the code`() {
        val pill = toLanguagePills(listOf("ja"), listOf(LanguageOption("ja", ""))).single()

        assertEquals(languageName("ja"), pill.name)
    }

    @Test
    fun `resolve gives chosen codes their names from the list`() {
        val resolved = listOf(english, japanese).resolve(listOf("ja", "xx"))

        assertEquals(listOf(japanese, LanguageOption("xx", "")), resolved)
        assertEquals("English", listOf(english).nameOf("en"))
        assertEquals(languageName("xx"), listOf(english).nameOf("xx"))
    }
}
