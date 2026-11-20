package com.zc.vndb.feature.detail.presentation.component.detailhero.mapper

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
}
