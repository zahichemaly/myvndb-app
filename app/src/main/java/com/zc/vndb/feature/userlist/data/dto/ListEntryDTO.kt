package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry from `POST /ulist`. */
@Serializable
data class ListEntryDTO(
    @SerialName("id") val id: String,
    @SerialName("vote") val vote: Int?,
    @SerialName("lastmod") val lastMod: Long,
    @SerialName("started") val started: String?,
    @SerialName("finished") val finished: String?,
    @SerialName("notes") val notes: String?,
    @SerialName("labels") val labels: List<EntryLabelDTO>?,
    @SerialName("vn") val vn: EntryNovelDTO,
)
