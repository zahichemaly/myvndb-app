package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListLabelDTO(
    @SerialName("id") val id: Int,
    @SerialName("label") val label: String,
    @SerialName("private") val private: Boolean?,
    @SerialName("count") val count: Int?,
)
