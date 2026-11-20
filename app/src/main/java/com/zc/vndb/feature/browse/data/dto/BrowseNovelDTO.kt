package com.zc.vndb.feature.browse.data.dto

import com.zc.vndb.common.data.dto.DeveloperDTO
import com.zc.vndb.common.data.dto.ImageDTO
import com.zc.vndb.common.data.dto.TagDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrowseNovelDTO(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String?,
    @SerialName("alttitle") val alttitle: String?,
    @SerialName("released") val released: String?,
    @SerialName("rating") val rating: Double?,
    @SerialName("length") val length: Int?,
    @SerialName("length_minutes") val lengthMinutes: Int?,
    @SerialName("image") val image: ImageDTO?,
    @SerialName("developers") val developers: List<DeveloperDTO>?,
    @SerialName("tags") val tags: List<TagDTO>?,
    /** Every language with a release, original included. */
    @SerialName("languages") val languages: List<String>?,
)
