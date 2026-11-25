package com.zc.vndb.feature.detail.presentation.component.detailtabs

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.DetailTab
import com.zc.vndb.feature.detail.presentation.model.DetailTabUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

/** Scrollable chip-style tabs inside a card. */
@Composable
fun DetailTabRowView(
    tabs: ImmutableList<DetailTabUIModel>,
    selected: DetailTab,
    onSelect: (DetailTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColors = TintedColors(
        container = VndbTheme.colors.accent,
        content = VndbTheme.colors.onAccent,
        border = VndbTheme.colors.accent,
    )
    val listState = rememberLazyListState()
    val selectedIndex = tabs.indexOfFirst { it.tab == selected }
    // A tab can be selected from outside the row ("See all"): keep it fully in view, also once
    // its label grows (its count arrives after it is selected).
    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) return@LaunchedEffect
        listState.revealItem(selectedIndex)
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex }?.size
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { listState.revealItem(selectedIndex) }
    }
    SectionCardView(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            items(items = tabs, key = { it.tab.name }) { tab ->
                SelectableChipView(
                    label = tab.label,
                    isSelected = tab.tab == selected,
                    onClick = { onSelect(tab.tab) },
                    selectedColors = selectedColors,
                )
            }
        }
    }
}

/** Scrolls only as far as needed for item [index] to be fully visible. */
private suspend fun LazyListState.revealItem(index: Int) {
    val layout = layoutInfo
    val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
    if (item == null) {
        animateScrollToItem(index)
        return
    }
    val overflowAtEnd = item.offset + item.size - layout.viewportEndOffset
    val overflowAtStart = layout.viewportStartOffset - item.offset
    when {
        overflowAtEnd > 0 -> animateScrollBy(overflowAtEnd.toFloat())
        overflowAtStart > 0 -> animateScrollBy(-overflowAtStart.toFloat())
    }
}

@AdaptivePreviews
@Composable
private fun DetailTabRowViewPreview() {
    VndbTheme {
        DetailTabRowView(
            tabs = DetailTab.entries.map {
                DetailTabUIModel(
                    it,
                    UiText.StringValue(it.name),
                )
            }.toImmutableList(),
            selected = DetailTab.Overview,
            onSelect = {},
        )
    }
}
