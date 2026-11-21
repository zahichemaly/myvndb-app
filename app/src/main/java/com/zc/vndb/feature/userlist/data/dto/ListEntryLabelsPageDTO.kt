package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListEntryLabelsPageDTO(@SerialName("results") val results: List<ListEntryLabelsDTO>)
