package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListEntryPageDTO(
    @SerialName("results") val results: List<ListEntryDTO>,
    @SerialName("more") val more: Boolean,
)
