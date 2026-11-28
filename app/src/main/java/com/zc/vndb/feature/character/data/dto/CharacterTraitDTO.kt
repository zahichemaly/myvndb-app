package com.zc.vndb.feature.character.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterTraitDTO(
    @SerialName("name") val name: String,
    @SerialName("group_name") val groupName: String?,
    @SerialName("spoiler") val spoiler: Int?,
    @SerialName("lie") val lie: Boolean?,
    @SerialName("sexual") val sexual: Boolean?,
)
