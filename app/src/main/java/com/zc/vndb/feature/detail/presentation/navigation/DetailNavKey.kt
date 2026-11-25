package com.zc.vndb.feature.detail.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** A visual novel's detail page. */
@Serializable
data class DetailNavKey(val id: String) : NavKey
