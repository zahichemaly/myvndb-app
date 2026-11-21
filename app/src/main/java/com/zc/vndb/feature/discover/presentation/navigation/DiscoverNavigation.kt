package com.zc.vndb.feature.discover.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zc.vndb.feature.discover.presentation.DiscoverRoute

fun EntryProviderScope<NavKey>.discoverEntry(
    onOpenNovel: (id: String) -> Unit,
    onOpenRelease: (id: String) -> Unit,
    onOpenBrowse: () -> Unit,
) {
    entry<DiscoverNavKey> {
        DiscoverRoute(
            onOpenNovel = onOpenNovel,
            onOpenRelease = onOpenRelease,
            onOpenBrowse = onOpenBrowse,
        )
    }
}
