package com.zc.vndb.feature.detail.data.dto

import com.zc.vndb.common.data.dto.DeveloperDTO
import com.zc.vndb.common.data.dto.ExtLinkDTO
import com.zc.vndb.common.data.dto.ImageDTO
import com.zc.vndb.common.data.dto.TagDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailNovelDTO(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String?,
    @SerialName("alttitle") val alttitle: String?,
    @SerialName("released") val released: String?,
    @SerialName("rating") val rating: Double?,
    @SerialName("average") val average: Double?,
    @SerialName("votecount") val votecount: Int?,
    @SerialName("length") val length: Int?,
    @SerialName("length_minutes") val lengthMinutes: Int?,
    @SerialName("image") val image: ImageDTO?,
    @SerialName("developers") val developers: List<DeveloperDTO>?,
    @SerialName("description") val description: String?,
    @SerialName("tags") val tags: List<TagDTO>?,
    @SerialName("extlinks") val extlinks: List<ExtLinkDTO>?,
    @SerialName("relations") val relations: List<RelationDTO>?,
    @SerialName("titles") val titles: List<NovelTitleDTO>?,
    @SerialName("aliases") val aliases: List<String>?,
    @SerialName("length_votes") val lengthVotes: Int?,
    /** The language the novel was first written in. */
    @SerialName("olang") val olang: String?,
    /** Every language with a release, original included. */
    @SerialName("languages") val languages: List<String>?,
)
