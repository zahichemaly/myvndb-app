package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The signed-in user's own entry from `POST /ulist`, without the VN fields. */
@Serializable
data class MyListEntryDTO(
    @SerialName("id") val id: String,
    @SerialName("vote") val vote: Int?,
    @SerialName("started") val started: String?,
    @SerialName("finished") val finished: String?,
    @SerialName("notes") val notes: String?,
    @SerialName("labels") val labels: List<EntryLabelDTO>?,
)
