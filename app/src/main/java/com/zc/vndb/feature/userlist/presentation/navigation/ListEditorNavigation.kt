package com.zc.vndb.feature.userlist.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zc.vndb.common.ui.navigation.BottomSheetSceneStrategy
import com.zc.vndb.common.ui.navigation.Navigator
import com.zc.vndb.feature.userlist.presentation.ListEditorRoute

@OptIn(ExperimentalMaterial3Api::class) // The list editor's bottom sheet.
fun EntryProviderScope<NavKey>.listEditorEntry(onClose: () -> Unit) {
    entry<ListEditorNavKey>(metadata = BottomSheetSceneStrategy.bottomSheet()) { key ->
        ListEditorRoute(
            novelId = key.novelId,
            novelTitle = key.novelTitle,
            onClose = onClose,
        )
    }
}

/** Opens the list editor sheet for a VN. */
fun Navigator.navigateToListEditor(novelId: String, novelTitle: String) {
    navigate(ListEditorNavKey(novelId, novelTitle))
}
