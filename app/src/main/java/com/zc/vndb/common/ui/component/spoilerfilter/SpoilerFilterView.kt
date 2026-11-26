package com.zc.vndb.common.ui.component.spoilerfilter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.spoilerfilter.mapper.toIcon
import com.zc.vndb.common.ui.component.spoilerfilter.model.SpoilerLevel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val ShieldSize = 16.dp
private val LevelIconSize = 14.dp

/**
 * "SPOILER", then None | Minor | Major sharing the rest of the line. The options stay together and
 * move under the label when they don't fit beside it, so no label is ever squeezed or wrapped.
 */
@Composable
fun SpoilerFilterView(
    level: SpoilerLevel,
    onLevelSelect: (SpoilerLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCardView(modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(ShieldSize),
                )
                Text(
                    text = UiText.StringResource(StringResource.detail_spoiler_filter).asString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            // Weighted: FlowRow still breaks the line by the options' natural width, then
            // stretches them over whatever is left of the line they land on.
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SpoilerLevel.entries.forEach { option ->
                    LevelOption(
                        label = option.toLabel(),
                        icon = option.toIcon(),
                        isSelected = option == level,
                        onClick = { onLevelSelect(option) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelOption(
    label: UiText,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    val color = if (isSelected) VndbTheme.colors.onAccent else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = VndbTheme.spacing.xs,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) VndbTheme.colors.accent else VndbTheme.colors.surfaceCard)
            // Bordered like the page's tab chips (Overview, Media…).
            .border(
                width = VndbTheme.dimens.borderWidth,
                color = if (isSelected) VndbTheme.colors.accent else VndbTheme.colors.borderSubtle,
                shape = shape,
            )
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { selected = isSelected }
            .padding(horizontal = VndbTheme.spacing.sm, vertical = VndbTheme.spacing.xs),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(LevelIconSize),
        )
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            softWrap = false,
            color = color,
        )
    }
}

private fun SpoilerLevel.toLabel(): UiText = UiText.StringResource(
    when (this) {
        SpoilerLevel.None -> StringResource.detail_spoiler_none
        SpoilerLevel.Minor -> StringResource.detail_spoiler_minor
        SpoilerLevel.Major -> StringResource.detail_spoiler_major
    },
)

@AdaptivePreviews
@Composable
private fun SpoilerFilterViewPreview() {
    VndbTheme {
        SpoilerFilterView(
            level = SpoilerLevel.None,
            onLevelSelect = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
