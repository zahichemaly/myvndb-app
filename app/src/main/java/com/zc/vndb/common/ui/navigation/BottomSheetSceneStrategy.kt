package com.zc.vndb.common.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.zc.vndb.common.ui.component.bottomsheet.BottomSheetView

/** Shows an entry in a [BottomSheetView] over the entries below it (Navigation 3 bottom-sheet recipe). */
internal data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val lifecycleOwner = rememberLifecycleOwner()
        BottomSheetView(onDismissRequest = onBack) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                entry.Content()
            }
        }
    }
}

/** Puts entries marked with [bottomSheet] in a [BottomSheetScene]. Goes before non-overlay strategies. */
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        if (lastEntry?.metadata?.get(BottomSheetKey) != true) return null
        @Suppress("UNCHECKED_CAST") // The content key of a NavKey entry is the key itself.
        return BottomSheetScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            onBack = onBack,
        )
    }

    companion object {
        /** Entry metadata that shows the entry in a bottom sheet ([BottomSheetView]). */
        fun bottomSheet() = metadata { put(BottomSheetKey, true) }

        object BottomSheetKey : NavMetadataKey<Boolean>
    }
}
