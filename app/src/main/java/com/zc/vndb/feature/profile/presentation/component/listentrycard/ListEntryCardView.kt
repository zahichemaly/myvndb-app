package com.zc.vndb.feature.profile.presentation.component.listentrycard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.coverimage.CoverImageView
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelChipView
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.component.languagepills.LanguagePillsView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.profile.presentation.component.listentrycard.model.ListEntryCardUIModel
import kotlinx.collections.immutable.persistentListOf

private val CoverWidth = 84.dp
private val SmallIconSize = 14.dp
private val NoteIconSize = 16.dp
private val EditButtonSize = 32.dp
private val EditIconSize = 18.dp
private const val NOTES_MAX_LINES = 2

/** A VN on the user's list: cover, titles, vote, dates, other labels, notes and last change. */
@Composable
fun ListEntryCardView(
    model: ListEntryCardUIModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .clickable(onClick = onClick)
            .padding(VndbTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
    ) {
        CoverImageView(
            url = model.coverUrl,
            isSensitive = model.isCoverSensitive,
            contentDescription = model.title,
            modifier = Modifier
                .width(CoverWidth)
                .aspectRatio(VndbTheme.dimens.coverAspectRatio)
                .clip(MaterialTheme.shapes.small),
        )
        // At least as tall as the cover, so "Updated" always ends the card in its bottom end
        // corner (under the edit button), however much sits above it.
        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = CoverWidth / VndbTheme.dimens.coverAspectRatio),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
                EntryTitles(model, Modifier.weight(1f))
                onEdit?.let { EditButton(onClick = it) }
            }
            EntryMeta(model)
            LanguagePillsView(model.languagePills)
            model.notes?.let { NotePill(it) }
            Spacer(Modifier.weight(1f))
            UpdatedLine(model.updatedLabel, Modifier.align(Alignment.End))
        }
    }
}

@Composable
private fun EditButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(EditButtonSize)) {
        Icon(
            Icons.Outlined.Edit,
            contentDescription = UiText.StringResource(
                StringResource.cd_edit_list_entry,
            ).asString(),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(EditIconSize),
        )
    }
}

@Composable
private fun EntryTitles(model: ListEntryCardUIModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        Text(
            text = model.title.asString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun UpdatedLine(label: UiText, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(SmallIconSize),
        )
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The user's note on this entry, in a tinted pill so it stands out from the details. */
@Composable
private fun NotePill(note: UiText) {
    val colors = VndbTheme.colors.chipNeutral
    val shape = MaterialTheme.shapes.small
    Row(
        modifier = Modifier
            .clip(shape)
            .background(colors.container)
            .border(VndbTheme.dimens.borderWidth, colors.border, shape)
            .padding(horizontal = VndbTheme.spacing.sm, vertical = VndbTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.Notes,
            contentDescription = null,
            tint = VndbTheme.colors.accent,
            modifier = Modifier.size(NoteIconSize),
        )
        Text(
            text = note.asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = NOTES_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Vote, start/finish dates and the entry's other labels, wrapping as needed. */
@Composable
private fun EntryMeta(model: ListEntryCardUIModel) {
    if (model.voteLabel == null && model.dates.isEmpty() && model.labels.isEmpty()) return
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        model.labels.forEach { EntryLabelChipView(it) }
        model.voteLabel?.let { vote ->
            TagChipView(
                label = vote,
                colors = VndbTheme.colors.spoilerMinor,
                icon = Icons.Filled.Star,
                contentDescription = UiText.StringResource(
                    StringResource.cd_your_vote,
                    listOf(vote),
                ),
            )
        }
        model.dates.forEach { TagChipView(label = it) }
    }
}

private const val PREVIEW_OWN_LABEL_ID = 13

@AdaptivePreviews
@Composable
private fun ListEntryCardViewPreview() {
    VndbTheme {
        ListEntryCardView(
            model = ListEntryCardUIModel(
                novelId = "v13631",
                title = UiText.StringValue("Hello Lady!"),
                coverUrl = null,
                isCoverSensitive = false,
                voteLabel = UiText.StringValue("8.5"),
                dates = persistentListOf(UiText.StringValue("Started 2024-10-15")),
                labels = persistentListOf(
                    EntryLabelUIModel(ListLabel.PLAYING, UiText.StringValue("Playing")),
                    EntryLabelUIModel(PREVIEW_OWN_LABEL_ID, UiText.StringValue("Waiting")),
                ),
                notes = UiText.StringValue("Currently on Sorako's route."),
                updatedLabel = UiText.StringValue("Updated 2 hours ago"),
            ),
            onClick = {},
        )
    }
}
