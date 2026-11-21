package com.zc.vndb.feature.userlist.presentation.navigation

import com.zc.vndb.common.ui.navigation.OverlayNavKey
import kotlinx.serialization.Serializable

/** The signed-in user's list entry for a VN, edited in a bottom sheet. */
@Serializable
data class ListEditorNavKey(val novelId: String, val novelTitle: String) : OverlayNavKey
