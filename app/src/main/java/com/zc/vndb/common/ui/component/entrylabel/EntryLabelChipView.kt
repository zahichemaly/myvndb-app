package com.zc.vndb.common.ui.component.entrylabel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val IconSize = 12.dp
private val HorizontalPadding = 8.dp
private val VerticalPadding = 2.dp

/** The tinted look of a reading status (Playing, Finished, Stalled, Dropped), else null. */
fun VndbColors.statusColors(id: Int): TintedColors? = when (id) {
    ListLabel.PLAYING -> statusPlaying
    ListLabel.FINISHED -> statusFinished
    ListLabel.STALLED -> statusStalled
    ListLabel.DROPPED -> statusDropped
    else -> null
}

/** The icon of a reading status: play, check, pause, cancel; null for other labels. */
fun statusIcon(id: Int): ImageVector? = when (id) {
    ListLabel.PLAYING -> Icons.Outlined.PlayCircle
    ListLabel.FINISHED -> Icons.Outlined.CheckCircle
    ListLabel.STALLED -> Icons.Outlined.PauseCircle
    ListLabel.DROPPED -> Icons.Outlined.Cancel
    else -> null
}

/**
 * The colored mark for a label in a filter tab: the status icon for statuses, the icon for Wishlist and
 * Blacklist, a star for Voted (in the vote color), a tag icon for your own labels, nothing for
 * No label.
 * Keeps its color whether or not the tab is selected.
 */
@Composable
fun EntryLabelMark(id: Int, modifier: Modifier = Modifier) {
    val colors = VndbTheme.colors
    val status = colors.statusColors(id)
    when {
        status != null -> statusIcon(id)?.let { MarkIcon(it, status.content, modifier) }

        id == ListLabel.VOTED -> MarkIcon(
            Icons.Filled.Star,
            colors.spoilerMinor.content,
            modifier,
        )

        id == ListLabel.WISHLIST -> MarkIcon(
            Icons.Outlined.BookmarkBorder,
            colors.labelWishlist.border,
            modifier,
        )

        id == ListLabel.BLACKLIST -> MarkIcon(
            Icons.Outlined.Block,
            colors.labelBlacklist.border,
            modifier,
        )

        id >= ListLabel.FIRST_USER_ID -> MarkIcon(
            Icons.Outlined.NewLabel,
            colors.labelOwn.content,
            modifier,
        )
    }
}

@Composable
private fun MarkIcon(icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(IconSize),
    )
}

/**
 * A list label. Statuses are tinted pills with a leading icon, colored by meaning; every other
 * label is outlined with an icon (Wishlist violet, Blacklist slate, your own labels neutral).
 */
@Composable
fun EntryLabelChipView(label: EntryLabelUIModel, modifier: Modifier = Modifier) {
    val colors = VndbTheme.colors
    val status = colors.statusColors(label.id)
    if (status != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            modifier = modifier
                .background(status.container, CircleShape)
                .border(VndbTheme.dimens.borderWidth, status.border, CircleShape)
                .padding(horizontal = HorizontalPadding, vertical = VerticalPadding),
        ) {
            statusIcon(label.id)?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = status.content,
                    modifier = Modifier.size(IconSize),
                )
            }
            ChipText(label.name, status.content)
        }
    } else {
        val (outline, icon) = when (label.id) {
            ListLabel.WISHLIST -> colors.labelWishlist to Icons.Outlined.BookmarkBorder
            ListLabel.BLACKLIST -> colors.labelBlacklist to Icons.Outlined.Block
            else -> colors.labelOwn to Icons.Outlined.NewLabel
        }
        OutlinedLabel(label.name, outline, icon, modifier)
    }
}

@Composable
private fun OutlinedLabel(
    name: UiText,
    colors: TintedColors,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        modifier = modifier
            .border(VndbTheme.dimens.borderWidth, colors.border, shape)
            .padding(horizontal = HorizontalPadding, vertical = VerticalPadding),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.content,
            modifier = Modifier.size(IconSize),
        )
        ChipText(name, colors.content)
    }
}

@Composable
private fun ChipText(text: UiText, color: Color) {
    Text(
        text = text.asString(),
        style = MaterialTheme.typography.labelMedium,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private const val PREVIEW_OWN_LABEL_ID = 13

@AdaptivePreviews
@Composable
private fun EntryLabelChipViewPreview() {
    val labels = mapOf(
        ListLabel.PLAYING to "Playing",
        ListLabel.FINISHED to "Finished",
        ListLabel.STALLED to "Stalled",
        ListLabel.DROPPED to "Dropped",
        ListLabel.WISHLIST to "Wishlist",
        ListLabel.BLACKLIST to "Blacklist",
        PREVIEW_OWN_LABEL_ID to "Ready to play",
    )
    VndbTheme {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            modifier = Modifier.padding(VndbTheme.spacing.sm),
        ) {
            labels.forEach { (id, name) ->
                EntryLabelChipView(EntryLabelUIModel(id, UiText.StringValue(name)))
            }
        }
    }
}
