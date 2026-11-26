package com.zc.vndb.common.ui.component.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/**
 * The app's modal bottom sheet; use it instead of Material's `ModalBottomSheet` (Detekt forbids
 * importing that anywhere else). It opens at its full content height (a taller one scrolls), so
 * Back dismisses it in one press instead of first collapsing it, and never goes above the status bar.
 *
 * Its content color always comes from the theme. Our sheet colors (`surfaceCard`,
 * `surfaceElevated`) aren't color-scheme colors, so Material's default would fall back to the
 * ambient content color, black. The sheet's own window also styles its system bars from that
 * color: black made them light, with dark icons and a white navigation bar on dark themes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = VndbTheme.colors.surfaceCard,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Fully qualified: the import is forbidden everywhere, so the other sheets come through here.
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        // Stops the sheet at the status bar. Material otherwise pads the content's top by however
        // far the sheet reaches under it, so the sheet's height (and resting spot) changes with its
        // position: a fling that overshoots there from just below bounces up and down for good.
        modifier = modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
        ),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        content = content,
    )
}

@AdaptivePreviews
@Composable
private fun BottomSheetViewPreview() {
    VndbTheme {
        BottomSheetView(onDismissRequest = {}) {
            Text(text = "Sheet content", modifier = Modifier.padding(VndbTheme.spacing.lg))
        }
    }
}
