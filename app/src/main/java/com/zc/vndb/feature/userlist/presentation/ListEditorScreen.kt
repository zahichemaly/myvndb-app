package com.zc.vndb.feature.userlist.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
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

/** The slider moves in half points (votes are stored ×10): 1.0, 1.5, … 10.0. */
private const val VOTE_STEP = 5

/** Slider stops between the two ends, one per [VOTE_STEP]. */
private const val VOTE_STEPS = (MyListEntry.MAX_VOTE - MyListEntry.MIN_VOTE) / VOTE_STEP - 1

private const val TITLE_MAX_LINES = 2

private val ProgressSize = 18.dp

/** Keeps the sheet from jumping in height when the skeleton gives way to the fields. */
private val BodyMinHeight = 360.dp

/** Slimmer than Material's 16dp track and 44dp thumb. The touch target stays 48dp. */
private val VoteTrackHeight = 6.dp
private val VoteThumbSize = DpSize(width = 4.dp, height = 24.dp)
private val VoteTickRadius = 1.5.dp

private val ClearIconSize = 18.dp
private val DateBorderWidth = 1.dp

/** The list editor's content: the VN's title, status, labels, vote, dates and notes, then Save (and Remove). */
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
            Header(state.novelTitle)
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

/** The sheet's only heading: the VN's title. */
@Composable
private fun Header(title: UiText) {
    Text(
        text = title.asString(),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = TITLE_MAX_LINES,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.semantics { heading() },
    )
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
        NotesField(state, onIntent)
    }
}

/** A section: a heading (with optional [trailing] content at its end) above its [content]. */
@Composable
private fun Field(
    title: UiText,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            trailing?.invoke()
        }
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
    val hasVote = state.vote != null
    Field(
        title = UiText.StringResource(StringResource.list_editor_vote),
        // One label at the heading's end that reads "No vote" and turns into the vote as the slider moves.
        trailing = {
            val typography = MaterialTheme.typography
            Text(
                text = state.voteLabel.asString(),
                style = if (hasVote) typography.titleMedium else typography.bodyMedium,
                color = if (hasVote) VndbTheme.colors.accent else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    ) {
        // Clear is always there (disabled without a vote), so the slider never changes width.
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoteSlider(
                vote = state.vote,
                onVoteChange = { onIntent(ListEditorIntent.VoteChanged(it)) },
                modifier = Modifier.weight(1f),
            )
            ClearButton(
                contentDescription = UiText.StringResource(StringResource.cd_clear_vote),
                onClick = { onIntent(ListEditorIntent.VoteCleared) },
                enabled = hasVote,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // The thumb and track slots, for a slimmer slider.
@Composable
private fun VoteSlider(vote: Int?, onVoteChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val accent = VndbTheme.colors.accent
    val colors = SliderDefaults.colors(
        thumbColor = accent,
        activeTrackColor = if (vote != null) accent else MaterialTheme.colorScheme.onSurfaceVariant,
        inactiveTrackColor = VndbTheme.colors.surfaceInset,
        activeTickColor = VndbTheme.colors.onAccent,
        inactiveTickColor = MaterialTheme.colorScheme.outline,
    )
    Slider(
        value = (vote ?: MyListEntry.MIN_VOTE).toFloat(), // No vote rests at 1.0
        onValueChange = { onVoteChange(it.roundToInt()) },
        modifier = modifier,
        colors = colors,
        interactionSource = interactionSource,
        steps = VOTE_STEPS,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = colors,
                thumbSize = VoteThumbSize,
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier.height(VoteTrackHeight),
                colors = colors,
                // A dot on every half point, the last one included (no separate stop indicator).
                drawStopIndicator = null,
                drawTick = { offset, color ->
                    drawCircle(color = color, radius = VoteTickRadius.toPx(), center = offset)
                },
            )
        },
        valueRange = MyListEntry.MIN_VOTE.toFloat()..MyListEntry.MAX_VOTE.toFloat(),
    )
}

@Composable
private fun DatesField(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Field(UiText.StringResource(StringResource.list_editor_dates)) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
            DateCell(
                title = UiText.StringResource(StringResource.list_editor_started),
                value = state.startedLabel,
                canClear = state.canClearStarted,
                isError = state.startedError != null,
                onPick = { onIntent(ListEditorIntent.DateClicked(DateField.Started)) },
                onClear = { onIntent(ListEditorIntent.DateCleared(DateField.Started)) },
                modifier = Modifier.weight(1f),
            )
            DateCell(
                title = UiText.StringResource(StringResource.list_editor_finished),
                value = state.finishedLabel,
                canClear = state.canClearFinished,
                isError = state.finishedError != null,
                onPick = { onIntent(ListEditorIntent.DateClicked(DateField.Finished)) },
                onClear = { onIntent(ListEditorIntent.DateCleared(DateField.Finished)) },
                modifier = Modifier.weight(1f),
            )
        }
        // Errors read in full under both dates; the red border says which one they're about.
        state.startedError?.let { DatesNote(it, isError = true) }
        state.finishedError?.let { DatesNote(it, isError = true) }
        state.datesHint?.let { DatesNote(it, isError = false) }
    }
}

/** A date as a compact outlined box: its name above the date (or "Set date"), and Clear at the end. */
@Composable
private fun DateCell(
    title: UiText,
    value: UiText?,
    canClear: Boolean,
    isError: Boolean,
    onPick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = LocalMinimumInteractiveComponentSize.current)
            .clip(shape)
            .border(
                width = DateBorderWidth,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onPick)
            .padding(start = VndbTheme.spacing.md),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = VndbTheme.spacing.xs)
                .padding(end = if (canClear) 0.dp else VndbTheme.spacing.md),
        ) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Without a date it reads "Set date", in the action color.
            val colors = MaterialTheme.colorScheme
            val label = value ?: UiText.StringResource(StringResource.list_editor_set_date)
            Text(
                text = label.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (value != null) colors.onSurface else colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (canClear) {
            ClearButton(
                contentDescription = UiText.StringResource(StringResource.cd_clear_date),
                onClick = onClear,
            )
        }
    }
}

@Composable
private fun DatesNote(text: UiText, isError: Boolean) {
    Text(
        text = text.asString(),
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun NotesField(state: ListEditorState, onIntent: (ListEditorIntent) -> Unit) {
    Field(UiText.StringResource(StringResource.list_editor_notes)) {
        OutlinedTextField(
            value = state.notes,
            onValueChange = { onIntent(ListEditorIntent.NotesChanged(it)) },
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = UiText.StringResource(StringResource.list_editor_notes_hint).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            isError = state.notesError != null,
            supportingText = { Text((state.notesError ?: state.notesCounter).asString()) },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ClearButton(contentDescription: UiText, onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = contentDescription.asString(),
            modifier = Modifier.size(ClearIconSize),
        )
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
        previewState.copy(
            finishedLabel = UiText.StringValue("2026-10-30"),
            canClearFinished = true,
            finishedError = UiText.StringValue("Finished can't be before Started."),
            datesHint = UiText.StringValue("VNDB can change a saved date but not remove it."),
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
