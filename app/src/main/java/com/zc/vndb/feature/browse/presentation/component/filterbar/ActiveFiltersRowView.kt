package com.zc.vndb.feature.browse.presentation.component.filterbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.browse.domain.model.AdvancedFilterKind
import com.zc.vndb.feature.browse.domain.model.Genre
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterKey
import com.zc.vndb.feature.browse.presentation.model.ActiveFilterUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val CloseIconSize = 14.dp

/** The filters that are on, as chips that remove themselves when tapped, plus "Clear all". */
@Composable
fun ActiveFiltersRowView(
    filters: ImmutableList<ActiveFilterUIModel>,
    onRemove: (ActiveFilterKey) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(items = filters, key = { it.key.id }) { filter ->
            ActiveFilterChip(filter = filter, onClick = { onRemove(filter.key) })
        }
        item(key = "clear-all") { ClearAllButton(onClick = onClearAll) }
    }
}

/**
 * "Clear all" as text, with the same minimum height as a pill. A [TextButton] is taller, so the
 * row grew when it scrolled into view and pushed the list below it down.
 */
@Composable
private fun ClearAllButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = VndbTheme.dimens.filterChipHeight)
            .clip(VndbTheme.shapes.pill)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VndbTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = UiText.StringResource(StringResource.browse_filters_clear_all).asString(),
            style = MaterialTheme.typography.labelLarge,
            color = VndbTheme.colors.accent,
            maxLines = 1,
        )
    }
}

@Composable
private fun ActiveFilterChip(filter: ActiveFilterUIModel, onClick: () -> Unit) {
    val shape = VndbTheme.shapes.pill
    val removeDescription = UiText.StringResource(
        StringResource.browse_filters_remove,
        listOf(filter.label),
    ).asString()
    Row(
        modifier = Modifier
            .heightIn(min = VndbTheme.dimens.filterChipHeight)
            .clip(shape)
            .background(VndbTheme.colors.accent)
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.accent, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .clearAndSetSemantics { contentDescription = removeDescription }
            .padding(start = VndbTheme.spacing.md, end = VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = filter.label.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = VndbTheme.colors.onAccent,
            maxLines = 1,
        )
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = null,
            tint = VndbTheme.colors.onAccent,
            modifier = Modifier.size(CloseIconSize),
        )
    }
}

@AdaptivePreviews
@Composable
private fun ActiveFiltersRowViewPreview() {
    VndbTheme {
        ActiveFiltersRowView(
            filters = persistentListOf(
                ActiveFilterUIModel(ActiveFilterKey.Age, UiText.StringValue("All Ages")),
                ActiveFilterUIModel(
                    ActiveFilterKey.GenreFilter(Genre.Mystery),
                    UiText.StringValue("Mystery"),
                ),
                ActiveFilterUIModel(
                    ActiveFilterKey.Advanced(AdvancedFilterKind.Length),
                    UiText.StringValue("Short"),
                ),
            ),
            onRemove = {},
            onClearAll = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
