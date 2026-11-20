package com.zc.vndb.feature.browse.domain.model

import com.zc.vndb.common.domain.model.NovelLength
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdvancedFiltersTest {

    private val all = AdvancedFilters(
        supportedLanguages = setOf("en", "ja"),
        originalLanguage = "ja",
        lengths = setOf(NovelLength.Short, NovelLength.Medium),
        era = ReleaseEra.In2010s,
    )

    @Test
    fun `nothing on is empty`() {
        assertTrue(AdvancedFilters().isEmpty)
        assertEquals(0, AdvancedFilters().activeCount)
    }

    @Test
    fun `several languages or lengths count as one filter each`() {
        assertEquals(4, all.activeCount)
        assertEquals(1, AdvancedFilters(lengths = NovelLength.entries.toSet()).activeCount)
        assertEquals(1, AdvancedFilters(supportedLanguages = setOf("en", "es", "ko")).activeCount)
    }

    @Test
    fun `without switches off only that filter`() {
        assertEquals(
            emptySet(),
            all.without(AdvancedFilterKind.SupportedLanguages).supportedLanguages,
        )
        assertEquals(null, all.without(AdvancedFilterKind.OriginalLanguage).originalLanguage)
        assertEquals(emptySet(), all.without(AdvancedFilterKind.Length).lengths)
        assertEquals(null, all.without(AdvancedFilterKind.Era).era)
        assertEquals(3, all.without(AdvancedFilterKind.Era).activeCount)
    }
}
