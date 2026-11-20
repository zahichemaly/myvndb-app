package com.zc.vndb.feature.browse.data.mapper

import com.zc.vndb.common.data.dto.QueryDTO
import com.zc.vndb.common.domain.model.NovelLength
import com.zc.vndb.feature.browse.domain.model.AdvancedFilters
import com.zc.vndb.feature.browse.domain.model.AgeRating
import com.zc.vndb.feature.browse.domain.model.BrowseFilter
import com.zc.vndb.feature.browse.domain.model.BrowseScope
import com.zc.vndb.feature.browse.domain.model.BrowseSort
import com.zc.vndb.feature.browse.domain.model.ReleaseEra
import com.zc.vndb.feature.browse.domain.model.isDescending
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

private const val ADULT_MIN_AGE = 18

private const val BROWSE_FIELDS = "title, alttitle, released, rating, length, length_minutes, " +
    "image.url, image.thumbnail, image.sexual, image.violence, developers.name, " +
    "tags.name, tags.rating, tags.spoiler, tags.category"

/** The `POST /vn` body for one page of [BrowseFilter] results. Expects a normalised filter. */
fun BrowseFilter.toQueryDTO(page: Int, pageSize: Int, withCount: Boolean): QueryDTO = QueryDTO(
    filters = toFilters(),
    fields = BROWSE_FIELDS,
    sort = sort.apiField,
    reverse = isDescending,
    results = pageSize,
    page = page,
    count = withCount.takeIf { it },
)

private fun BrowseFilter.toFilters(): JsonElement {
    val adultRelease = predicate("minage", ">=", JsonPrimitive(ADULT_MIN_AGE))
    val predicates = buildList {
        when (ageRating) {
            AgeRating.AllAges -> add(predicate("release", "!=", adultRelease))
            AgeRating.Adult -> add(predicate("release", "=", adultRelease))
            AgeRating.Any -> Unit
        }
        if (query.isNotEmpty()) add(predicate("search", "=", JsonPrimitive(query)))
        // VNDB sorts unknown (TBA) dates last-is-newest; "Newest" should mean already released.
        if (sort == BrowseSort.Newest) add(predicate("released", "<=", JsonPrimitive("today")))
        genres.sortedBy { it.ordinal }.forEach {
            add(
                predicate("tag", "=", JsonPrimitive(it.tagId)),
            )
        }
        addAll(advanced.toPredicates())
        scope.toPredicate()?.let(::add)
    }
    // No condition at all ("Both" ages, nothing else) is an empty list: VNDB rejects ["and"].
    return when (predicates.size) {
        0 -> JsonArray(emptyList())

        1 -> predicates.single()

        else -> buildJsonArray {
            add("and")
            predicates.forEach { add(it) }
        }
    }
}

/** One condition per filter that is on; several languages or lengths share one `or`. */
private fun AdvancedFilters.toPredicates(): List<JsonArray> = buildList {
    addAnyOf(
        supportedLanguages
            .sorted()
            .map { predicate("lang", "=", JsonPrimitive(it)) },
    )
    originalLanguage?.let { add(predicate("olang", "=", JsonPrimitive(it))) }
    addAnyOf(
        lengths
            .sortedBy { it.ordinal }
            .map { predicate("length", "=", JsonPrimitive(it.apiValue)) },
    )
    era?.let { add(it.toPredicate()) }
}

/** Adds [predicates] as one condition: the single one, or an `or` of several; nothing if empty. */
private fun MutableList<JsonArray>.addAnyOf(predicates: List<JsonArray>) {
    when (predicates.size) {
        0 -> Unit

        1 -> add(predicates.single())

        else -> add(
            buildJsonArray {
                add("or")
                predicates.forEach { add(it) }
            },
        )
    }
}

/** VNDB's length filter counts from 1 (very short) to 5 (very long); 0 means unknown. */
private val NovelLength.apiValue: Int get() = ordinal + 1

private fun ReleaseEra.toPredicate(): JsonArray = when (this) {
    ReleaseEra.Since2020 -> predicate("released", ">=", JsonPrimitive("2020"))
    ReleaseEra.In2010s -> between("2010", "2020")
    ReleaseEra.In2000s -> between("2000", "2010")
    ReleaseEra.Before2000 -> predicate("released", "<", JsonPrimitive("2000"))
}

/** Released in [from] (inclusive) up to [until] (exclusive). */
private fun between(from: String, until: String): JsonArray = buildJsonArray {
    add("and")
    add(predicate("released", ">=", JsonPrimitive(from)))
    add(predicate("released", "<", JsonPrimitive(until)))
}

/** The condition a tag, producer or staff page adds; null for the Browse tab. */
private fun BrowseScope.toPredicate(): JsonArray? = when (this) {
    BrowseScope.None -> null

    is BrowseScope.Tag -> predicate("tag", "=", JsonPrimitive(id))

    is BrowseScope.Producer ->
        predicate("developer", "=", predicate("id", "=", JsonPrimitive(id)))

    is BrowseScope.Staff -> predicate("staff", "=", predicate("id", "=", JsonPrimitive(id)))
}

private val BrowseSort.apiField: String
    get() = when (this) {
        BrowseSort.Relevance -> "searchrank"
        BrowseSort.Popularity -> "votecount"
        BrowseSort.Rating -> "rating"
        BrowseSort.Newest -> "released"
        BrowseSort.Title -> "title"
    }

private fun predicate(name: String, operator: String, value: JsonElement): JsonArray =
    buildJsonArray {
        add(name)
        add(operator)
        add(value)
    }

/** One tag by id, for the tag page header. */
fun tagQuery(id: String) = QueryDTO(
    filters = predicate("id", "=", JsonPrimitive(id)),
    fields = "name, aliases, description, category, vn_count",
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)

/** One producer by id, for the producer page header. */
fun producerQuery(id: String) = QueryDTO(
    filters = predicate("id", "=", JsonPrimitive(id)),
    fields = "name, original, aliases, lang, type, description",
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)

/** One staff member by id, for the staff page header. */
fun staffQuery(id: String) = QueryDTO(
    filters = predicate("id", "=", JsonPrimitive(id)),
    fields = "name, original, lang, gender, aliases.name, aliases.latin, description, " +
        "extlinks.label, extlinks.url",
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)
