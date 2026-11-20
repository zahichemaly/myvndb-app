package com.zc.vndb.feature.detail.presentation.component.editionrow.mapper

import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.DetailSamples.edition
import com.zc.vndb.feature.detail.presentation.component.editionrow.model.PlatformIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EditionRowUIModelMapperTest {

    @Test
    fun `maps platform, languages, date and platform icon`() {
        val row = edition(
            "r1",
            platform = "xbo",
        ).copy(languageCodes = listOf("en", "ja")).toRowUIModel()

        assertEquals("r1-xbo", row.key)
        assertEquals("r1", row.releaseId)
        assertEquals(UiText.StringValue("Xbox One"), row.platformLabel)
        assertEquals(UiText.StringValue("2010-01-01"), row.dateLabel)
        assertEquals(listOf(PlatformIcon.Xbox), row.platformIcons)
        assertNull(row.morePlatformsLabel)
        assertEquals(2, (row.languagesLabel as UiText.StringValue).value.split(", ").size)
        assertNull(edition("r2").copy(languageCodes = emptyList()).toRowUIModel().languagesLabel)
    }

    @Test
    fun `an edition without a platform shows no icon`() {
        assertTrue(edition("r1", platform = "").toRowUIModel().platformIcons.isEmpty())
    }
}
