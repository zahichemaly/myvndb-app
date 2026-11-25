package com.zc.vndb.feature.userlist.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The signed-in user's list entry for a VN, edited in a bottom sheet. */
@Serializable
data class ListEditorNavKey(val novelId: String, val novelTitle: String) : NavKey
