package com.zc.vndb.common.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /schema`; only the enum lists are read, the field and link documentation is ignored. */
@Serializable
data class SchemaDTO(@SerialName("enums") val enums: SchemaEnumsDTO)

@Serializable
data class SchemaEnumsDTO(@SerialName("language") val language: List<LanguageDTO>)

@Serializable
data class LanguageDTO(@SerialName("id") val id: String, @SerialName("label") val label: String)
