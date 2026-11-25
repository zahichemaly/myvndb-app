package com.zc.vndb.feature.release.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** A release's page. */
@Serializable
data class ReleaseNavKey(val id: String) : NavKey
