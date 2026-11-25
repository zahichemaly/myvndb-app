package com.zc.vndb.common.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.zc.vndb.common.ui.theme.VndbTheme

/** Shows an entry in a [ModalBottomSheet] over the entries below it (Navigation 3 bottom-sheet recipe). */
@OptIn(ExperimentalMaterial3Api::class)
internal data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val properties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val lifecycleOwner = rememberLifecycleOwner()
        // No half-open state: the sheet opens at its full content height (a taller one scrolls) and
        // Back dismisses it in one press instead of first collapsing it.
        ModalBottomSheet(
            onDismissRequest = onBack,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            properties = properties,
            containerColor = VndbTheme.colors.surfaceCard,
            // Explicit: surfaceCard isn't a color-scheme color, so the default falls back to the
            // ambient content color (black). The sheet's own window also styles its system bars
            // from this color, and black made them light (dark icons, a white navigation bar).
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                entry.Content()
            }
        }
    }
}

/** Puts entries marked with [bottomSheet] in a [BottomSheetScene]. Goes before non-overlay strategies. */
@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val properties = lastEntry?.metadata?.get(BottomSheetKey) ?: return null
        @Suppress("UNCHECKED_CAST") // The content key of a NavKey entry is the key itself.
        return BottomSheetScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            properties = properties,
            onBack = onBack,
        )
    }

    companion object {
        /** Entry metadata that shows the entry in a bottom sheet. */
        fun bottomSheet(properties: ModalBottomSheetProperties = ModalBottomSheetProperties()) =
            metadata { put(BottomSheetKey, properties) }

        object BottomSheetKey : NavMetadataKey<ModalBottomSheetProperties>
    }
}
