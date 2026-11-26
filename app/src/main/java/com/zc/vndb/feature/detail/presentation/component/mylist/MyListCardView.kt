package com.zc.vndb.feature.detail.presentation.component.mylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelChipView
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelUIModel
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.component.mylist.model.MyListCardUIModel
import kotlinx.collections.immutable.persistentListOf

private val ButtonIconSize = 18.dp
private val VoteStarSize = 20.dp

/** "My list": the user's status, labels, vote, dates and notes for this VN, with Add / Edit. */
@Composable
fun MyListCardView(model: MyListCardUIModel, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    SectionCardView(
        modifier = modifier,
        title = UiText.StringResource(StringResource.detail_my_list),
        trailing = if (model.isOnList && model.canEdit) {
            { EditButton(onEdit) }
        } else {
            null
        },
    ) {
        if (model.isOnList) Entry(model) else NotOnList(model, onEdit)
        if (!model.canEdit) {
            Text(
                text = UiText.StringResource(StringResource.detail_my_list_read_only).asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EditButton(onEdit: () -> Unit) {
    TextButton(onClick = onEdit) {
        Icon(
            Icons.Outlined.Edit,
            contentDescription = null,
            modifier = Modifier.size(ButtonIconSize),
        )
        Spacer(Modifier.size(VndbTheme.spacing.xs))
        Text(
            text = UiText.StringResource(StringResource.detail_my_list_edit).asString(),
            color = VndbTheme.colors.accent,
        )
    }
}

@Composable
private fun NotOnList(model: MyListCardUIModel, onEdit: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        Text(
            text = UiText.StringResource(StringResource.detail_my_list_empty).asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (model.canEdit) {
            Button(
                onClick = onEdit,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonIconSize),
                )
                Spacer(Modifier.size(VndbTheme.spacing.sm))
                Text(UiText.StringResource(StringResource.list_editor_add).asString())
            }
        }
    }
}

@Composable
private fun Entry(model: MyListCardUIModel) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        if (model.labels.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) { model.labels.forEach { EntryLabelChipView(it) } }
        }
        model.voteLabel?.let { vote ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = VndbTheme.colors.score,
                    modifier = Modifier.size(VoteStarSize),
                )
                Text(
                    text = vote.asString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = VndbTheme.colors.score,
                )
            }
        }
        model.dates.forEach { date ->
            Text(
                text = date.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        model.notes?.let { notes ->
            Text(
                text = notes.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private const val PREVIEW_OWN_LABEL_ID = 13

private class MyListCardProvider : PreviewParameterProvider<MyListCardUIModel> {
    override val values = sequenceOf(
        MyListCardUIModel(
            isOnList = true,
            canEdit = true,
            labels = persistentListOf(
                EntryLabelUIModel(ListLabel.PLAYING, UiText.StringValue("Playing")),
                EntryLabelUIModel(ListLabel.WISHLIST, UiText.StringValue("Wishlist")),
                EntryLabelUIModel(PREVIEW_OWN_LABEL_ID, UiText.StringValue("Ready to play")),
            ),
            voteLabel = UiText.StringValue("8.5"),
            dates = persistentListOf(UiText.StringValue("Started 2026-11-01")),
            notes = UiText.StringValue("Route order: Reika last."),
        ),
        MyListCardUIModel(isOnList = false, canEdit = true),
        MyListCardUIModel(isOnList = false, canEdit = false),
    )
}

@AdaptivePreviews
@Composable
private fun MyListCardViewPreview(
    @PreviewParameter(
        MyListCardProvider::class,
    ) model: MyListCardUIModel,
) {
    VndbTheme {
        MyListCardView(
            model = model,
            onEdit = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
