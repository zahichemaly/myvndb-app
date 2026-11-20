package com.zc.vndb.feature.browse.presentation.mapper

import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.common.ui.mapper.languageName
import com.zc.vndb.common.ui.mapper.toLanguagesLabel
import com.zc.vndb.common.ui.mapper.toNameLabel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.browse.domain.model.AdvancedFilterKind
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdvancedFilterUIModelMapperTest {

    private fun pills(filter: BrowseFilter, default: AgeRating = AgeRating.AllAges) =
        filter.toActiveFilters(default)

    @Test
    fun `nothing applied, no pills`() {
        assertTrue(pills(BrowseFilter()).isEmpty())
    }

    @Test
    fun `the age rating is a pill only when it is not the starting one`() {
        assertTrue(
            pills(BrowseFilter(ageRating = AgeRating.Any), default = AgeRating.Any).isEmpty(),
        )

        val pill = pills(BrowseFilter(ageRating = AgeRating.Adult)).single()

        assertEquals(ActiveFilterKey.Age, pill.key)
        assertEquals(UiText.StringResource(StringResource.browse_age_adult), pill.label)
    }

    @Test
    fun `each genre is its own pill, in the genre order`() {
        val pills = pills(BrowseFilter(genres = setOf(Genre.Horror, Genre.Romance)))

        assertEquals(
            listOf(
                ActiveFilterKey.GenreFilter(Genre.Romance),
                ActiveFilterKey.GenreFilter(Genre.Horror),
            ),
            pills.map { it.key },
        )
        assertEquals(
            UiText.StringResource(StringResource.browse_genre_romance),
            pills.first().label,
        )
    }

    @Test
    fun `a pill per advanced filter in the sheet's order`() {
        val pills = pills(
            BrowseFilter(
                advanced = AdvancedFilters(
                    supportedLanguages = setOf("ja", "en"),
                    originalLanguage = "ko",
                    lengths = setOf(NovelLength.Short),
                    era = ReleaseEra.In2010s,
                ),
            ),
        )

        assertEquals(
            listOf(
                AdvancedFilterKind.SupportedLanguages,
                AdvancedFilterKind.OriginalLanguage,
                AdvancedFilterKind.Length,
                AdvancedFilterKind.Era,
            ).map { ActiveFilterKey.Advanced(it) },
            pills.map { it.key },
        )
        assertEquals(
            UiText.StringResource(
                StringResource.browse_filter_chip_supported,
                listOf(listOf("en", "ja").toLanguagesLabel()),
            ),
            pills[0].label,
        )
        assertEquals(
            UiText.StringResource(
                StringResource.browse_filter_chip_original,
                listOf(languageName("ko")),
            ),
            pills[1].label,
        )
        assertEquals(NovelLength.Short.toNameLabel(), pills[2].label)
        assertEquals(UiText.StringResource(StringResource.browse_era_2010s), pills[3].label)
    }

    @Test
    fun `several lengths share one pill that says how many`() {
        val pill = pills(
            BrowseFilter(
                advanced = AdvancedFilters(lengths = setOf(NovelLength.Long, NovelLength.Short)),
            ),
        ).single()

        assertEquals(
            UiText.StringPlural(PluralResource.browse_filter_chip_lengths, 2, listOf(2)),
            pill.label,
        )
    }

    @Test
    fun `pill ids are unique so the row can key on them`() {
        val ids = pills(
            BrowseFilter(
                ageRating = AgeRating.Adult,
                genres = setOf(Genre.Mystery),
                advanced = AdvancedFilters(originalLanguage = "ja", era = ReleaseEra.Since2020),
            ),
        ).map { it.key.id }

        assertEquals(ids.distinct(), ids)
    }
}
