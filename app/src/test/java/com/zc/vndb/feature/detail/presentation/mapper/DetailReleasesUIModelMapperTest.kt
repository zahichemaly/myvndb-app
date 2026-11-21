package com.zc.vndb.feature.detail.presentation.mapper

import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.DetailSamples.releaseSummary
import kotlin.test.Test
import kotlin.test.assertEquals

class DetailReleasesUIModelMapperTest {

    @Test
    fun `releases are grouped per language, biggest group first`() {
        val groups = listOf(
            releaseSummary("r1", listOf("en")),
            releaseSummary("r2", listOf("ja")),
            releaseSummary("r3", listOf("ja", "en", "zh-Hans")),
            releaseSummary("r4", listOf("ja")),
        ).toReleaseGroups()

        assertEquals(listOf("ja", "en", "zh-Hans"), groups.map { it.languageCode })
        assertEquals(UiText.StringValue("🇯🇵 Japanese (3)"), groups.first().title)
        assertEquals(UiText.StringValue("🇬🇧 English (2)"), groups[1].title)
        assertEquals(listOf("en-r1", "en-r3"), groups[1].releases.map { it.key })
        assertEquals(listOf(PlatformIcon.Windows), groups[1].releases.first().platformIcons)
    }

    @Test
    fun `releases without a language get an unflagged group`() {
        val group = listOf(releaseSummary("r1", emptyList())).toReleaseGroups().single()

        assertEquals(UiText.StringValue("? (1)"), group.title)
    }
}
