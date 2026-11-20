package com.zc.vndb.feature.profile.data.mapper

import com.zc.vndb.feature.profile.data.dto.ListQueryDTO
import com.zc.vndb.feature.profile.domain.model.EntryOrder
import com.zc.vndb.feature.profile.domain.model.MyListEntry
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

private const val ENTRY_FIELDS =
    "id, vote, lastmod, started, finished, notes, labels.id, labels.label, " +
        "vn.title, vn.alttitle, vn.released, vn.image.url, vn.image.thumbnail, vn.image.sexual, vn.image.violence, vn.languages"

/** VNDB's virtual "Voted" label: every entry with a vote. */
private const val VOTED_LABEL = 7

/**
 * A page of [userId]'s entries with [labelId] in [order], [ascending] or not. With [voted], only
 * entries that also have (true) or lack (false) the Voted label.
 */
fun entriesQuery(
    userId: String,
    labelId: Int,
    page: Int,
    pageSize: Int,
    order: EntryOrder = EntryOrder.Updated,
    ascending: Boolean = false,
    voted: Boolean? = null,
) = ListQueryDTO(
    user = userId,
    filters = if (voted == null) {
        labelFilter("=", labelId)
    } else {
        buildJsonArray {
            add("and")
            add(labelFilter("=", labelId))
            add(labelFilter(if (voted) "=" else "!=", VOTED_LABEL))
        }
    },
    fields = ENTRY_FIELDS,
    sort = when (order) {
        EntryOrder.Title -> "title"
        EntryOrder.Updated -> "lastmod"
        EntryOrder.Vote -> "vote"
    },
    reverse = !ascending,
    results = pageSize,
    page = page,
)

private fun labelFilter(operator: String, labelId: Int) = buildJsonArray {
    add("label")
    add(operator)
    add(labelId)
}

/** The labels [userId] gave [novelId] (no results when it is not on their list). */
fun entryLabelsQuery(userId: String, novelId: String) = ListQueryDTO(
    user = userId,
    filters = buildJsonArray {
        add("id")
        add("=")
        add(novelId)
    },
    fields = "labels.id, labels.label",
    sort = "id",
    reverse = false,
    results = 1,
    page = 1,
)

private const val MY_ENTRY_FIELDS = "id, vote, started, finished, notes, labels.id, labels.label"

/** [userId]'s own entry for [novelId] (no results when it is not on their list). */
fun myEntryQuery(userId: String, novelId: String) = ListQueryDTO(
    user = userId,
    filters = buildJsonArray {
        add("id")
        add("=")
        add(novelId)
    },
    fields = MY_ENTRY_FIELDS,
    sort = "id",
    reverse = false,
    results = 1,
    page = 1,
)

/**
 * Body of `PATCH /ulist/<id>` with only what changed since [previous] (null: not on the list yet,
 * so every field that is set). `labels` overwrites the labels (VNDB keeps "Voted" in sync with
 * `vote`) and a null vote removes the vote. VNDB only takes a real date for `started` and
 * `finished` (no null or empty string), so a date is sent only when it is set and changed.
 */
fun MyListEntry.toPatchBody(previous: MyListEntry?): JsonObject = buildJsonObject {
    val before = previous ?: MyListEntry(novelId = novelId)
    val labels = labelIds.withoutVirtual()
    if (previous == null || labels != before.labelIds.withoutVirtual()) {
        putJsonArray("labels") { labels.sorted().forEach { add(it) } }
    }
    if (vote != before.vote) put("vote", vote?.let(::JsonPrimitive) ?: JsonNull)
    if (started != null && started != before.started) put("started", started.toString())
    if (finished != null && finished != before.finished) put("finished", finished.toString())
    val text = notes?.takeIf { it.isNotBlank() }
    if (text != before.notes?.takeIf { it.isNotBlank() }) put("notes", text.orEmpty())
}

private fun Set<Int>.withoutVirtual(): Set<Int> =
    filterNot { it in MyListEntry.VIRTUAL_LABELS }.toSet()
