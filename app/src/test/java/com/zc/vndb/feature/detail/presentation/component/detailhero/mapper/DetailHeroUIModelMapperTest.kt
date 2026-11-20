package com.zc.vndb.feature.detail.presentation.component.detailhero.mapper

import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.ui.mapper.languageName
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.detail.domain.model.AgeRange
import com.zc.vndb.feature.detail.domain.model.DetailSamples.novel
import com.zc.vndb.feature.detail.domain.model.DetailSamples.tag
import com.zc.vndb.feature.detail.presentation.component.detailhero.model.HeroDeveloperUIModel
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DetailHeroUIModelMapperTest {

    @Test
    fun `maps display values`() {
        val hero = novel(
            tags = listOf(
                tag("A", 1.0),
                tag("B", 3.0),
                tag("C", 2.0),
                tag("Spoiler", 3.0, spoiler = 1),
            ),
        )
            .toHeroUIModel(AgeRange(0, 15))

        assertEquals(
            UiText.StringResource(
                StringResource.detail_score_value,
                listOf(UiText.StringValue("9.02")),
            ),
            hero.scoreLabel,
        )
        assertEquals(
            listOf(
                HeroDeveloperUIModel(id = "p1", name = "NITRO PLUS"),
                HeroDeveloperUIModel(id = null, name = "MAGES."),
            ),
            hero.developers,
        )
        assertEquals(UiText.StringValue("2009-10-15"), hero.releasedLabel)
        assertEquals(UiText.StringResource(StringResource.length_name_long), hero.lengthName)
        assertEquals(
            UiText.StringResource(
                StringResource.detail_length_value,
                listOf(UiText.StringResource(StringResource.detail_hours, listOf("44"))),
            ),
            hero.lengthValueLabel,
        )
        assertEquals(
            UiText.StringResource(StringResource.detail_length_votes, listOf("1,021")),
            hero.lengthVotesLabel,
        )
        assertEquals(
            listOf(
                TagChipUIModel("g-B", UiText.StringValue("B")),
                TagChipUIModel("g-C", UiText.StringValue("C")),
            ),
            hero.tags,
        )
        assertEquals(
            UiText.StringResource(StringResource.detail_age_range_from_all, listOf(15)),
            hero.ageLabel,
        )
    }

    @Test
    fun `age labels`() {
        assertEquals(UiText.StringResource(StringResource.detail_age_all), AgeRange(0, 0).toLabel())
        assertEquals(
            UiText.StringResource(StringResource.detail_age_min, listOf(18)),
            AgeRange(18, 18).toLabel(),
        )
        assertEquals(
            UiText.StringResource(StringResource.detail_age_range, listOf(12, 18)),
            AgeRange(12, 18).toLabel(),
        )
        assertNull(novel().toHeroUIModel(ageRange = null).ageLabel)
    }

    @Test
    fun `without votes the length falls back to the category range`() {
        val hero = novel().copy(averagePlayMinutes = null).toHeroUIModel(null)

        assertEquals(
            UiText.StringResource(
                StringResource.detail_length_value,
                listOf(UiText.StringResource(StringResource.length_long)),
            ),
            hero.lengthValueLabel,
        )
        assertNull(hero.lengthVotesLabel)
    }

    private val options = listOf(
        LanguageOption("en", "English"),
        LanguageOption("ja", "Japanese"),
        LanguageOption("ko", "Korean"),
        LanguageOption("zh-Hans", "Chinese (simplified)"),
        LanguageOption("es", "Spanish"),
        LanguageOption("ar", "Arabic"),
        LanguageOption("eu", "Basque"),
    )

    @Test
    fun `the original language shows with its flag and name`() {
        val hero = novel(originalLanguage = "ja").toHeroUIModel(null, languageOptions = options)

        assertEquals("🇯🇵 Japanese", hero.originalLanguage?.label)
        assertEquals("ja", hero.originalLanguage?.code)
    }

    @Test
    fun `available in lists two languages besides the original, common ones first`() {
        val hero = novel(
            originalLanguage = "ja",
            languages = listOf("ar", "ja", "es", "en", "ko", "zh-Hans", "eu"),
        ).toHeroUIModel(null, languageOptions = options)

        assertEquals(listOf("en", "zh-Hans"), hero.availableLanguages.map { it.code })
        assertEquals(
            listOf("🇬🇧 English", "🇨🇳 Chinese (simplified)"),
            hero.availableLanguages.map { it.label },
        )
        assertEquals(4, hero.moreLanguagesCount)
    }

    @Test
    fun `the original language is never listed under available in`() {
        val hero = novel(originalLanguage = "en", languages = listOf("en", "ja", "ko"))
            .toHeroUIModel(null, languageOptions = options)

        assertEquals(listOf("ja", "ko"), hero.availableLanguages.map { it.code })
        assertEquals(0, hero.moreLanguagesCount)
    }

    @Test
    fun `exactly two translations show no count`() {
        val hero = novel(originalLanguage = "ja", languages = listOf("ja", "en", "ko"))
            .toHeroUIModel(null, languageOptions = options)

        assertEquals(2, hero.availableLanguages.size)
        assertEquals(0, hero.moreLanguagesCount)
    }

    @Test
    fun `a novel with only its original language has nothing under available in`() {
        val hero = novel(originalLanguage = "ja", languages = listOf("ja"))
            .toHeroUIModel(null, languageOptions = options)

        assertEquals("ja", hero.originalLanguage?.code)
        assertTrue(hero.availableLanguages.isEmpty())
        assertEquals(0, hero.moreLanguagesCount)
    }

    @Test
    fun `no language data hides both rows`() {
        val hero = novel(originalLanguage = null, languages = emptyList())
            .toHeroUIModel(null, languageOptions = options)

        assertNull(hero.originalLanguage)
        assertTrue(hero.availableLanguages.isEmpty())
    }

    @Test
    fun `languages outside the common ones follow A to Z, and names fall back to the device`() {
        val hero = novel(originalLanguage = "ja", languages = listOf("ja", "eu", "ar", "ca"))
            .toHeroUIModel(null)

        assertEquals(listOf("ar", "eu"), hero.availableLanguages.map { it.code })
        assertEquals(1, hero.moreLanguagesCount)
        assertEquals(languageName("ar"), hero.availableLanguages.first().name)
    }
}
