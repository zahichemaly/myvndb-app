package com.zc.vndb.feature.userlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.entrylabel.EntryLabelMark
import com.zc.vndb.common.ui.component.entrylabel.statusColors
import com.zc.vndb.common.ui.component.error.ErrorView
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipDefaults
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.presentation.component.ListEditorDialogs
import com.zc.vndb.feature.userlist.presentation.component.editorskeleton.EditorSkeletonView
import com.zc.vndb.feature.userlist.presentation.model.DateField
import com.zc.vndb.feature.userlist.presentation.model.LabelOptionUIModel
import kotlin.math.roundToInt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Slider stops: every tenth of a point from 1.0 to 10.0. */
private const val VOTE_STEPS = MyListEntry.MAX_VOTE - MyListEntry.MIN_VOTE - 1

private val ProgressSize = 18.dp

/** Keeps the sheet from jumping in height when the skeleton gives way to the fields. */
private val BodyMinHeight = 360.dp

/** The list editor's content: status, labels, vote, dates and notes, then Save (and Remove). */
@Composable
fun ListEditorScreen(
    state: ListEditorState,
    onIntent: (ListEditorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showFooter = !state.isLoading && state.error == null
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        // Only the fields scroll: Save and Remove stay pinned below them.
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(min = BodyMinHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VndbTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
        ) {
            Header(state)
            when {
                state.isLoading -> EditorSkeletonView(modifier = Modifier.fillMaxWidth())

                state.error != null -> ErrorView(
                    model = state.error,
                    onRetry = { onIntent(ListEditorIntent.Retry) },
                    modifier = Modifier.fillMaxWidth(),
                )

                else -> EditorFields(state, onIntent)
            }
        }
        if (showFooter) {
            Footer(state, onIntent)
        }
    }
    ListEditorDialogs(dialog = state.dialog, onIntent = onIntent)
}

@Composable
private fun Footer(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Column(
        modifier = Modifier.padding(
            start = VndbTheme.spacing.lg,
            end = VndbTheme.spacing.lg,
            top = VndbTheme.spacing.sm,
            bottom = VndbTheme.spacing.lg,
        ),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        state.saveError?.let { error ->
            Text(
                text = error.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Actions(state, onIntent)
    }
}

@Composable
private fun Header(state: ListEditorState) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
        Text(
            text = UiText.StringResource(
                if (state.isOnList) StringResource.list_editor_title_edit else StringResource.list_editor_title_add,
            ).asString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = state.novelTitle.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EditorFields(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg)) {
        Field(UiText.StringResource(StringResource.list_editor_status)) {
            ChipFlow(state.statuses) { onIntent(ListEditorIntent.StatusSelected(it)) }
        }
        if (state.otherLabels.isNotEmpty()) {
            Field(UiText.StringResource(StringResource.list_editor_labels)) {
                ChipFlow(state.otherLabels) { onIntent(ListEditorIntent.LabelToggled(it)) }
            }
        }
        VoteField(state, onIntent)
        DatesField(state, onIntent)
        Field(UiText.StringResource(StringResource.list_editor_notes)) {
            OutlinedTextField(
                value = state.notes,
                onValueChange = { onIntent(ListEditorIntent.NotesChanged(it)) },
                placeholder = {
                    Text(
                        UiText.StringResource(StringResource.list_editor_notes_hint).asString(),
                    )
                },
                isError = state.notesError != null,
                supportingText = { Text((state.notesError ?: state.notesCounter).asString()) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Field(title: UiText, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        Text(
            text = title.asString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun ChipFlow(options: ImmutableList<LabelOptionUIModel>, onClick: (Int) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        options.forEach { option ->
            SelectableChipView(
                label = option.label,
                isSelected = option.isSelected,
                onClick = { onClick(option.id) },
                // The same dot / icon as the status and label pills elsewhere.
                leading = { EntryLabelMark(option.id) },
                selectedColors = VndbTheme.colors.statusColors(option.id)
                    ?: SelectableChipDefaults.colors(),
            )
        }
    }
}

@Composable
private fun VoteField(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Field(UiText.StringResource(StringResource.list_editor_vote)) {
        // One label that reads "No vote" and turns into the vote as the slider moves. The row is
        // always as tall as the Clear vote button, so the button appearing moves nothing.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.heightIn(min = LocalMinimumInteractiveComponentSize.current),
        ) {
            Text(
                text = state.voteLabel.asString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (state.vote !=
                    null
                ) {
                    VndbTheme.colors.accent
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f),
            )
            if (state.vote != null) {
                TextButton(onClick = { onIntent(ListEditorIntent.VoteCleared) }) {
                    Text(UiText.StringResource(StringResource.list_editor_clear_vote).asString())
                }
            }
        }
        Slider(
            value = (state.vote ?: MyListEntry.MIN_VOTE).toFloat(), // No vote rests at 1.0
            onValueChange = { onIntent(ListEditorIntent.VoteChanged(it.roundToInt())) },
            valueRange = MyListEntry.MIN_VOTE.toFloat()..MyListEntry.MAX_VOTE.toFloat(),
            steps = VOTE_STEPS,
            colors = SliderDefaults.colors(
                thumbColor = VndbTheme.colors.accent,
                activeTrackColor = if (state.vote != null) {
                    VndbTheme.colors.accent
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                inactiveTrackColor = VndbTheme.colors.surfaceInset,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun DatesField(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Field(UiText.StringResource(StringResource.list_editor_dates)) {
        DateRow(
            title = UiText.StringResource(StringResource.list_editor_started),
            value = state.startedLabel,
            canClear = state.canClearStarted,
            error = state.startedError,
            onPick = { onIntent(ListEditorIntent.DateClicked(DateField.Started)) },
            onClear = { onIntent(ListEditorIntent.DateCleared(DateField.Started)) },
        )
        DateRow(
            title = UiText.StringResource(StringResource.list_editor_finished),
            value = state.finishedLabel,
            canClear = state.canClearFinished,
            error = state.finishedError,
            onPick = { onIntent(ListEditorIntent.DateClicked(DateField.Finished)) },
            onClear = { onIntent(ListEditorIntent.DateCleared(DateField.Finished)) },
        )
        state.datesHint?.let { hint ->
            Text(
                text = hint.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DateRow(
    title: UiText,
    value: UiText?,
    canClear: Boolean,
    error: UiText?,
    onPick: () -> Unit,
    onClear: () -> Unit,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = onPick) {
                val label = value ?: UiText.StringResource(StringResource.list_editor_set_date)
                Text(label.asString())
            }
            if (canClear) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = UiText.StringResource(
                            StringResource.cd_clear_date,
                        ).asString(),
                    )
                }
            }
        }
        error?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun Actions(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.isOnList) {
            TextButton(
                onClick = { onIntent(ListEditorIntent.RemoveClicked) },
                enabled = !state.isSaving,
            ) {
                Text(
                    text = UiText.StringResource(StringResource.list_editor_remove).asString(),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onIntent(ListEditorIntent.SaveClicked) },
            enabled = !state.isSaving && state.isValid,
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ProgressSize),
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    UiText.StringResource(
                        if (state.isOnList) StringResource.list_editor_save else StringResource.list_editor_add,
                    ).asString(),
                )
            }
        }
    }
}

private val previewState = ListEditorState(
    novelTitle = UiText.StringValue("Hello, Lady!"),
    isLoading = false,
    isOnList = true,
    statuses = persistentListOf(
        LabelOptionUIModel(1, UiText.StringValue("Playing"), isSelected = true),
        LabelOptionUIModel(2, UiText.StringValue("Finished"), isSelected = false),
        LabelOptionUIModel(3, UiText.StringValue("Stalled"), isSelected = false),
        LabelOptionUIModel(4, UiText.StringValue("Dropped"), isSelected = false),
    ),
    otherLabels = persistentListOf(
        LabelOptionUIModel(5, UiText.StringValue("Wishlist"), isSelected = false),
        LabelOptionUIModel(6, UiText.StringValue("Blacklist"), isSelected = false),
    ),
    vote = 85,
    voteLabel = UiText.StringValue("8.5"),
    startedLabel = UiText.StringValue("2026-11-01"),
    notes = "Route order: Reika last.",
)

private class ListEditorStateProvider : PreviewParameterProvider<ListEditorState> {
    override val values = sequenceOf(
        previewState,
        previewState.copy(
            isOnList = false,
            vote = null,
            voteLabel = UiText.StringValue("No vote"),
            startedLabel = null,
        ),
        previewState.copy(isLoading = true),
    )
}

@AdaptivePreviews
@Composable
private fun ListEditorScreenPreview(
    @PreviewParameter(
        ListEditorStateProvider::class,
    ) state: ListEditorState,
) {
    VndbTheme {
        Surface(color = VndbTheme.colors.surfaceCard) {
            ListEditorScreen(state = state, onIntent = {})
        }
    }
}
