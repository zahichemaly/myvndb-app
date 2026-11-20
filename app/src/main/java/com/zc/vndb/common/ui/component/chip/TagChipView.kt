package com.zc.vndb.common.ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val HorizontalPadding = 6.dp
private val VerticalPadding = 2.dp
private val IconSize = 12.dp

/**
 * Compact mono chip for tags, platforms and short metadata. Neutral unless [colors] is given.
 * [icon] goes before the label in the label's color; [contentDescription] replaces what screen
 * readers announce (e.g. "Your vote: 8.5" for "★ 8.5"). With [onClick] the chip is a button and
 * its label takes the accent color, like other tappable text.
 */
@Composable
fun TagChipView(
    label: UiText,
    modifier: Modifier = Modifier,
    colors: TintedColors = VndbTheme.colors.chipNeutral,
    icon: ImageVector? = null,
    contentDescription: UiText? = null,
    onClick: (() -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.small
    val description = contentDescription?.asString()
    val contentColor = if (onClick != null) VndbTheme.colors.accent else colors.content
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clip(shape).clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .background(colors.container, shape)
            .border(VndbTheme.dimens.borderWidth, colors.border, shape)
            .padding(horizontal = HorizontalPadding, vertical = VerticalPadding)
            .then(
                if (description != null) {
                    Modifier.clearAndSetSemantics { this.contentDescription = description }
                } else {
                    Modifier
                },
            ),
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(IconSize),
            )
        }
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@AdaptivePreviews
@Composable
private fun TagChipViewPreview() {
    VndbTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            modifier = Modifier.padding(VndbTheme.spacing.sm),
        ) {
            TagChipView(label = UiText.StringValue("Urban Fantasy"), onClick = {})
            TagChipView(label = UiText.StringValue("Windows"))
            TagChipView(
                label = UiText.StringValue("All Ages"),
                colors = VndbTheme.colors.spoilerNone,
            )
        }
    }
}
