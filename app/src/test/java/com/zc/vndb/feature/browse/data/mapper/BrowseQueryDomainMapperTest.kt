package com.zc.vndb.feature.browse.data.mapper

import com.zc.vndb.common.data.network.appJson
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

class BrowseQueryDomainMapperTest {

    private fun json(text: String) = appJson.parseToJsonElement(text)

    private fun BrowseFilter.body(page: Int = 1, withCount: Boolean = true): JsonObject =
        appJson.encodeToJsonElement(
            toQueryDTO(page = page, pageSize = 20, withCount = withCount),
        ).jsonObject

    @Test
    fun `default filter excludes adult releases, sorted by popularity, with count`() {
        val body = BrowseFilter().body()

        assertEquals(json("""["release","!=",["minage",">=",18]]"""), body["filters"])
        assertEquals(json("\"votecount\""), body["sort"])
        assertEquals(json("true"), body["reverse"])
        assertEquals(json("20"), body["results"])
        assertEquals(json("1"), body["page"])
        assertEquals(json("true"), body["count"])
    }

    @Test
    fun `adult, search and genres are combined with and`() {
        val filter = BrowseFilter(
            query = "steins",
            ageRating = AgeRating.Adult,
            genres = setOf(Genre.Mystery, Genre.Romance),
            sort = BrowseSort.Relevance,
        )

        val body = filter.body()

        assertEquals(
            json(
                """["and",["release","=",["minage",">=",18]],["search","=","steins"],""" +
                    """["tag","=","g96"],["tag","=","g19"]]""",
            ),
            body["filters"],
        )
        assertEquals(json("\"searchrank\""), body["sort"])
        assertEquals(json("false"), body["reverse"])
    }

    @Test
    fun `each sort maps to its api field and direction`() {
        val expected = mapOf(
            BrowseSort.Popularity to ("votecount" to true),
            BrowseSort.Rating to ("rating" to true),
            BrowseSort.Newest to ("released" to true),
            BrowseSort.Title to ("title" to false),
        )
        expected.forEach { (sort, fieldAndReverse) ->
            val body = BrowseFilter(sort = sort).body()
            assertEquals(json("\"${fieldAndReverse.first}\""), body["sort"])
            assertEquals(json("${fieldAndReverse.second}"), body["reverse"])
        }
    }

    @Test
    fun `a reversed filter flips the sort direction`() {
        assertEquals(json("false"), BrowseFilter(isReversed = true).body()["reverse"])
        assertEquals(
            json("true"),
            BrowseFilter(sort = BrowseSort.Title, isReversed = true).body()["reverse"],
        )
    }

    @Test
    fun `later pages omit count`() {
        val body = BrowseFilter().body(page = 3, withCount = false)

        assertEquals(json("3"), body["page"])
        assertFalse("count" in body)
    }

    @Test
    fun `newest only includes released titles`() {
        val body = BrowseFilter(sort = BrowseSort.Newest).body()

        assertEquals(
            json("""["and",["release","!=",["minage",">=",18]],["released","<=","today"]]"""),
            body["filters"],
        )
    }

    @Test
    fun `a tag page adds its tag to the filters`() {
        val body = BrowseFilter(scope = BrowseScope.Tag("g249")).body()

        assertEquals(
            json("""["and",["release","!=",["minage",">=",18]],["tag","=","g249"]]"""),
            body["filters"],
        )
    }

    @Test
    fun `a producer page filters to novels the producer developed`() {
        val body = BrowseFilter(scope = BrowseScope.Producer("p30")).body()

        assertEquals(
            json(
                """["and",["release","!=",["minage",">=",18]],["developer","=",["id","=","p30"]]]""",
            ),
            body["filters"],
        )
    }

    @Test
    fun `producer scope combines with search and genres`() {
        val body = BrowseFilter(
            ageRating = AgeRating.Any,
            query = "fate",
            genres = setOf(Genre.Mystery),
            scope = BrowseScope.Producer("p30"),
        ).body()

        assertEquals(
            json(
                """["and",["search","=","fate"],["tag","=","${Genre.Mystery.tagId}"],""" +
                    """["developer","=",["id","=","p30"]]]""",
            ),
            body["filters"],
        )
    }

    @Test
    fun `producer lookup by id`() {
        val body = appJson.encodeToJsonElement(producerQuery("p30")).jsonObject

        assertEquals(json("""["id","=","p30"]"""), body["filters"])
        assertEquals(json("\"name, original, aliases, lang, type, description\""), body["fields"])
    }

    @Test
    fun `a staff page filters to novels the person worked on`() {
        val body = BrowseFilter(scope = BrowseScope.Staff("s2")).body()

        assertEquals(
            json(
                """["and",["release","!=",["minage",">=",18]],["staff","=",["id","=","s2"]]]""",
            ),
            body["filters"],
        )
    }

    @Test
    fun `staff lookup by id`() {
        val body = appJson.encodeToJsonElement(staffQuery("s2")).jsonObject

        assertEquals(json("""["id","=","s2"]"""), body["filters"])
        assertEquals(
            json(
                "\"name, original, lang, gender, aliases.name, aliases.latin, description, " +
                    "extlinks.label, extlinks.url\"",
            ),
            body["fields"],
        )
    }

    @Test
    fun `tag lookup by id`() {
        val body = appJson.encodeToJsonElement(tagQuery("g249")).jsonObject

        assertEquals(json("""["id","=","g249"]"""), body["filters"])
        assertEquals(json("\"name, aliases, description, category, vn_count\""), body["fields"])
    }

    @Test
    fun `both ages alone is an empty filter list, which VNDB accepts`() {
        assertEquals(json("[]"), BrowseFilter(ageRating = AgeRating.Any).body()["filters"])
        assertEquals(
            json("""["search","=","steins"]"""),
            BrowseFilter(ageRating = AgeRating.Any, query = "steins").body()["filters"],
        )
    }

    private fun advanced(advanced: AdvancedFilters) =
        BrowseFilter(ageRating = AgeRating.Any, advanced = advanced).body()["filters"]

    @Test
    fun `supported languages become lang and the original language olang`() {
        assertEquals(
            json("""["lang","=","en"]"""),
            advanced(AdvancedFilters(supportedLanguages = setOf("en"))),
        )
        assertEquals(
            json("""["olang","=","ja"]"""),
            advanced(AdvancedFilters(originalLanguage = "ja")),
        )
    }

    @Test
    fun `several supported languages are any-of, in the offered order`() {
        assertEquals(
            json("""["or",["lang","=","en"],["lang","=","ja"],["lang","=","ko"]]"""),
            advanced(AdvancedFilters(supportedLanguages = setOf("ko", "en", "ja"))),
        )
    }

    @Test
    fun `lengths map to VNDB's 1 to 5 and several share one or`() {
        assertEquals(
            json("""["length","=",2]"""),
            advanced(AdvancedFilters(lengths = setOf(NovelLength.Short))),
        )
        assertEquals(
            json("""["or",["length","=",1],["length","=",5]]"""),
            advanced(AdvancedFilters(lengths = setOf(NovelLength.VeryLong, NovelLength.VeryShort))),
        )
    }

    @Test
    fun `each era is a release date range`() {
        assertEquals(
            json("""["released",">=","2020"]"""),
            advanced(AdvancedFilters(era = ReleaseEra.Since2020)),
        )
        assertEquals(
            json("""["and",["released",">=","2010"],["released","<","2020"]]"""),
            advanced(AdvancedFilters(era = ReleaseEra.In2010s)),
        )
        assertEquals(
            json("""["and",["released",">=","2000"],["released","<","2010"]]"""),
            advanced(AdvancedFilters(era = ReleaseEra.In2000s)),
        )
        assertEquals(
            json("""["released","<","2000"]"""),
            advanced(AdvancedFilters(era = ReleaseEra.Before2000)),
        )
    }

    @Test
    fun `advanced filters combine with age, genres and scope`() {
        val body = BrowseFilter(
            genres = setOf(Genre.Mystery),
            advanced = AdvancedFilters(
                supportedLanguages = setOf("en"),
                era = ReleaseEra.Since2020,
            ),
            scope = BrowseScope.Tag("g249"),
        ).body()

        assertEquals(
            json(
                """["and",["release","!=",["minage",">=",18]],["tag","=","${Genre.Mystery.tagId}"],""" +
                    """["lang","=","en"],["released",">=","2020"],["tag","=","g249"]]""",
            ),
            body["filters"],
        )
    }

    @Test
    fun `no advanced filters add nothing`() {
        assertEquals(json("[]"), advanced(AdvancedFilters()))
    }
}
