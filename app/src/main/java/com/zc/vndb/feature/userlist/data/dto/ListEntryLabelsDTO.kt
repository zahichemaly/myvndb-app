package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry from `POST /ulist` with only its labels. */
@Serializable
data class ListEntryLabelsDTO(
    @SerialName("id") val id: String,
    @SerialName("labels") val labels: List<EntryLabelDTO>?,
)
