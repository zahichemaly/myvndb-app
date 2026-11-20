package com.zc.vndb.common.ui.mapper

import com.zc.vndb.common.domain.model.CommonLanguageCodes
import com.zc.vndb.common.domain.model.LanguageOption
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageUIModelMapperTest {

    private fun option(code: String, label: String) = LanguageOption(code, label)

    @Test
    fun `the label is the display name`() {
        assertEquals(
            "Chinese (simplified)",
            option("zh-Hans", "Chinese (simplified)").displayName(),
        )
    }

    @Test
    fun `without a label the display name comes from the code`() {
        assertEquals(languageName("ja"), option("ja", "").displayName())
    }

    @Test
    fun `common languages come first in their usual order, the rest A to Z`() {
        val ordered = listOf(
            option("ar", "Arabic"),
            option("ja", "Japanese"),
            option("fi", "Finnish"),
            option("en", "English"),
            option("eu", "Basque"),
            option("ko", "Korean"),
        ).inPickerOrder()

        assertEquals(listOf("en", "ja", "ko", "ar", "eu", "fi"), ordered.map { it.code })
    }

    @Test
    fun `the rest is sorted without regard to case`() {
        val ordered = listOf(
            option("hu", "hungarian"),
            option("ga", "Irish"),
            option("el", "Greek"),
        )
            .inPickerOrder()

        assertEquals(listOf("el", "hu", "ga"), ordered.map { it.code })
    }

    @Test
    fun `common languages missing from the list are skipped`() {
        val ordered = listOf(option("ar", "Arabic"), option("ko", "Korean")).inPickerOrder()

        assertEquals(listOf("ko", "ar"), ordered.map { it.code })
        assertEquals(true, CommonLanguageCodes.first() == "en")
    }

    @Test
    fun `a language list label names up to two and counts the rest`() {
        assertEquals(
            "English, Japanese +1",
            listOf(
                "en",
                "ja",
                "ko",
            ).toLanguagesLabel { code ->
                mapOf(
                    "en" to "English",
                    "ja" to "Japanese",
                )[code] ?: code
            }
                .let { (it as com.zc.vndb.common.ui.text.UiText.StringValue).value },
        )
    }
}
