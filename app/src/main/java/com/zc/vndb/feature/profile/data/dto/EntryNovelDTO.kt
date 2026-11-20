package com.zc.vndb.feature.profile.data.dto

import com.zc.vndb.common.data.dto.ImageDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntryNovelDTO(
    @SerialName("title") val title: String,
    @SerialName("alttitle") val altTitle: String?,
    @SerialName("released") val released: String?,
    @SerialName("image") val image: ImageDTO?,
    /** Every language with a release, original included. */
    @SerialName("languages") val languages: List<String>?,
)
