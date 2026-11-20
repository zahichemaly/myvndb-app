package com.zc.vndb.feature.detail.data.mapper

import com.zc.vndb.common.data.dto.QueryDTO
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

/* The queries behind the detail page, one per section. Only the novel query runs on open. */

private const val MAX_RESULTS = 100

private const val NOVEL_FIELDS =
    "title, alttitle, released, rating, average, votecount, length, length_minutes, length_votes, " +
        "image.url, image.thumbnail, image.sexual, image.violence, developers.id, developers.name, description, " +
        "tags.name, tags.rating, tags.spoiler, tags.category, extlinks.label, extlinks.url, " +
        "relations.id, relations.title, relations.alttitle, relations.relation, relations.relation_official, " +
        "relations.released, " +
        "relations.rating, titles.lang, titles.title, titles.latin, titles.official, titles.main, aliases, olang, languages"

private const val CREDIT_FIELDS = "staff.id, staff.name, staff.role, staff.note, staff.eid, " +
    "editions.eid, editions.lang, editions.name, editions.official, va.character.id, va.staff.id, va.staff.name"

private const val SCREENSHOT_FIELDS =
    "screenshots.url, screenshots.thumbnail, screenshots.sexual, " +
        "screenshots.violence, screenshots.release.id, screenshots.release.title"

private const val CHARACTER_FIELDS = "name, original, image.url, image.sexual, image.violence, " +
    "vns.id, vns.role, vns.spoiler"

private const val EDITION_FIELDS = "title, released, platforms, languages.lang, official, patch, minage"

private const val RELEASE_FIELDS = "title, released, platforms, languages.lang, official, patch, freeware, minage"

private const val COVER_FIELDS = "title, images.type, images.url, images.thumbnail, images.sexual, images.violence"

private const val QUOTE_FIELDS = "quote, score, character.name"

fun novelQuery(id: String) = byId(id, NOVEL_FIELDS)

fun creditsQuery(novelId: String) = byId(novelId, CREDIT_FIELDS)

fun screenshotsQuery(novelId: String) = byId(novelId, SCREENSHOT_FIELDS)

fun charactersQuery(novelId: String) = ofNovel(novelId, CHARACTER_FIELDS)

fun editionsQuery(novelId: String) = ofNovel(novelId, EDITION_FIELDS, sort = "released")

fun coversQuery(novelId: String) = ofNovel(novelId, COVER_FIELDS, sort = "released")

fun releasesQuery(novelId: String, page: Int, pageSize: Int) =
    ofNovel(novelId, RELEASE_FIELDS, sort = "released", results = pageSize, page = page)

fun quotesQuery(novelId: String, page: Int, pageSize: Int) =
    ofNovel(novelId, QUOTE_FIELDS, sort = "score", reverse = true, results = pageSize, page = page)

/** The VN record itself, with only [fields]. */
private fun byId(id: String, fields: String) = QueryDTO(
    filters = predicate("id", "=", JsonPrimitive(id)),
    fields = fields,
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)

/** Characters, releases or quotes that belong to the VN. */
private fun ofNovel(
    novelId: String,
    fields: String,
    sort: String? = null,
    reverse: Boolean = false,
    results: Int = MAX_RESULTS,
    page: Int? = null,
) = QueryDTO(
    filters = predicate("vn", "=", predicate("id", "=", JsonPrimitive(novelId))),
    fields = fields,
    sort = sort,
    reverse = reverse.takeIf { sort != null },
    results = results,
    page = page,
    count = null,
)

private fun predicate(name: String, operator: String, value: JsonElement): JsonArray =
    buildJsonArray {
        add(name)
        add(operator)
        add(value)
    }
