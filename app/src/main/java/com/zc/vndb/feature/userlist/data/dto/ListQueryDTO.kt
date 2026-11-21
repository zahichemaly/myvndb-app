package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** Body of `POST /ulist`: a VN query plus the list owner. */
@Serializable
data class ListQueryDTO(
    @SerialName("user") val user: String,
    @SerialName("filters") val filters: JsonElement,
    @SerialName("fields") val fields: String,
    @SerialName("sort") val sort: String,
    @SerialName("reverse") val reverse: Boolean,
    @SerialName("results") val results: Int,
    @SerialName("page") val page: Int,
)
