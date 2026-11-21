package com.zc.vndb.feature.userlist.presentation.model

/** What the editor is opened for (one injected parameter, so Koin can't mix up two strings). */
data class ListEditorArgs(val novelId: String, val novelTitle: String)
