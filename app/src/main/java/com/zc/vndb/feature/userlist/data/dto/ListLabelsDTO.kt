package com.zc.vndb.feature.userlist.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `GET /ulist_labels`. */
@Serializable
data class ListLabelsDTO(@SerialName("labels") val labels: List<ListLabelDTO>)
