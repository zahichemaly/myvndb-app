package com.zc.vndb.feature.character.data.mapper

import com.zc.vndb.common.data.dto.QueryDTO
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

private const val MAX_NOVELS = 100

private const val CHARACTER_FIELDS = "name, original, aliases, description, " +
    "image.url, image.sexual, image.violence, age, birthday, blood_type, height, weight, bust, waist, hips, cup, " +
    "sex, gender, traits.name, traits.group_name, traits.spoiler, traits.lie, traits.sexual, " +
    "vns.id, vns.title, vns.alttitle, vns.released, vns.rating, " +
    "vns.image.url, vns.image.thumbnail, vns.image.sexual, vns.image.violence, vns.role, vns.spoiler"

fun characterQuery(id: String) = QueryDTO(
    filters = predicate("id", "=", JsonPrimitive(id)),
    fields = CHARACTER_FIELDS,
    sort = null,
    reverse = null,
    results = 1,
    page = null,
    count = null,
)

/** Every VN the character is in, with its voice-actor credits. */
fun characterVoicesQuery(characterId: String) = QueryDTO(
    filters = predicate("character", "=", predicate("id", "=", JsonPrimitive(characterId))),
    fields = "va.character.id, va.staff.id, va.staff.name",
    sort = null,
    reverse = null,
    results = MAX_NOVELS,
    page = null,
    count = null,
)

private fun predicate(name: String, operator: String, value: JsonElement): JsonArray =
    buildJsonArray {
        add(name)
        add(operator)
        add(value)
    }
