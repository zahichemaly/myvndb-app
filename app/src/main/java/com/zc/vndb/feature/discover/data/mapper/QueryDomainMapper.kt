package com.zc.vndb.feature.discover.data.mapper

import com.zc.vndb.common.data.dto.QueryDTO
import com.zc.vndb.common.domain.model.TOP_RATED_MIN_VOTES
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonArray
import kotlinx.serialization.json.buildJsonArray

/* Builds the VNDB queries Discover sends. Field lists name only what the domain models use. */

private const val NOVEL_ID_PREFIX = "v"

/** A release rated 18+ makes the whole visual novel adult, as in Browse. */
private const val ADULT_MIN_AGE = 18

private const val COVER_FIELDS = "image.url, image.thumbnail, image.sexual, image.violence"
private const val NOVEL_CARD_FIELDS = "title, alttitle, rating, $COVER_FIELDS, developers.name"
private const val NOVEL_DETAIL_FIELDS =
    "$NOVEL_CARD_FIELDS, description, staff.name, staff.role, " +
        "tags.name, tags.rating, tags.spoiler, tags.category"
private const val RELEASE_FIELDS = "released, patch, platforms, languages.lang, languages.mtl, " +
    "vns.id, vns.title, vns.alttitle, vns.olang, vns.released, vns.rating, " +
    "vns.image.url, vns.image.thumbnail, vns.image.sexual, vns.image.violence"

fun topRatedNovelsQuery(limit: Int) = QueryDTO(
    filters = filter("votecount", ">=", JsonPrimitive(TOP_RATED_MIN_VOTES)),
    fields = NOVEL_CARD_FIELDS,
    sort = "rating",
    reverse = true,
    results = limit,
    page = null,
    count = null,
)

fun novelDetailsQuery(id: String) = QueryDTO(
    filters = filter("id", "=", JsonPrimitive(id)),
    fields = NOVEL_DETAIL_FIELDS,
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)

/** Releases already out in any of [languageCodes], newest first; [page] counts from 1. */
fun recentReleasesQuery(languageCodes: List<String>, limit: Int, page: Int? = null) = QueryDTO(
    filters = buildJsonArray {
        add("and")
        add(filter("released", "<=", JsonPrimitive("today")))
        addJsonArray {
            add("or")
            languageCodes.forEach { code -> add(filter("lang", "=", JsonPrimitive(code))) }
        }
    },
    fields = RELEASE_FIELDS,
    sort = "released",
    reverse = true,
    results = limit,
    page = page,
    count = null,
)

fun latestNovelQuery() = QueryDTO(
    filters = JsonArray(emptyList()),
    fields = "id",
    sort = "id",
    reverse = true,
    results = 1,
    page = null,
    count = null,
)

fun firstNovelFromQuery(number: Int) = QueryDTO(
    filters = filter("id", ">=", JsonPrimitive("$NOVEL_ID_PREFIX$number")),
    fields = "id",
    sort = "id",
    reverse = false,
    results = 1,
    page = null,
    count = null,
)

/**
 * The best-scored quotes from visual novels with no 18+ release (the rule Browse's "All Ages"
 * uses). VNDB's own `random` filter cannot be combined with other filters, so Discover picks one of
 * these at random itself.
 */
fun topQuotesQuery(limit: Int) = QueryDTO(
    filters = buildJsonArray {
        add("vn")
        add("=")
        addJsonArray {
            add("release")
            add("!=")
            add(filter("minage", ">=", JsonPrimitive(ADULT_MIN_AGE)))
        }
    },
    fields = "quote, vn.id, vn.title, vn.alttitle, character.name",
    sort = "score",
    reverse = true,
    results = limit,
    page = null,
    count = null,
)

private fun filter(name: String, operator: String, value: JsonPrimitive): JsonArray =
    buildJsonArray {
        add(name)
        add(operator)
        add(value)
    }
