package com.zc.vndb.feature.userlist.presentation.mapper

import com.zc.vndb.common.domain.model.ListLabel
import com.zc.vndb.common.ui.resource.PluralResource
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.userlist.domain.model.ListEntryProblem
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.presentation.ListEditorState
import com.zc.vndb.feature.userlist.presentation.model.LabelOptionUIModel
import kotlin.time.Instant
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

private const val VOTE_SCALE = 10.0

/**
 * The editor's fields for [draft], with the user's [labels] split into statuses and the rest.
 * [previous] is the entry as loaded; [problems] are what VNDB would reject, shown per field.
 */
fun ListEditorState.withDraft(
    draft: MyListEntry,
    previous: MyListEntry?,
    labels: List<ListLabel>,
    problems: Set<ListEntryProblem>,
): ListEditorState {
    val options = labels.map { label ->
        LabelOptionUIModel(
            id = label.id,
            label = UiText.StringValue(label.name),
            isSelected = label.id in draft.labelIds,
        )
    }
    return copy(
        statuses = options.filter { it.id in MyListEntry.STATUS_LABELS }.toImmutableList(),
        otherLabels = options.filterNot { it.id in MyListEntry.STATUS_LABELS }.toImmutableList(),
        vote = draft.vote,
        voteLabel = draft.vote?.toVoteLabel()
            ?: UiText.StringResource(StringResource.list_editor_no_vote),
        startedLabel = draft.started?.let { UiText.StringValue(it.toString()) },
        finishedLabel = draft.finished?.let { UiText.StringValue(it.toString()) },
        canClearStarted = draft.started != null && previous?.started == null,
        canClearFinished = draft.finished != null && previous?.finished == null,
        startedError = problems.startedError(),
        finishedError = problems.finishedError(),
        datesHint = if (previous?.started != null || previous?.finished != null) {
            UiText.StringResource(StringResource.list_editor_dates_hint)
        } else {
            null
        },
        notes = draft.notes.orEmpty(),
        notesCounter = UiText.StringResource(
            StringResource.list_editor_notes_counter,
            listOf(draft.notes.orEmpty().length, MyListEntry.NOTES_MAX_LENGTH),
        ),
        notesError = UiText.StringPlural(
            PluralResource.list_editor_error_notes_length,
            MyListEntry.NOTES_MAX_LENGTH,
            listOf(MyListEntry.NOTES_MAX_LENGTH),
        ).takeIf { ListEntryProblem.NotesTooLong in problems },
        isValid = problems.isEmpty(),
    )
}

private fun Set<ListEntryProblem>.startedError(): UiText? = when {
    ListEntryProblem.StartedOutOfRange in this -> UiText.StringResource(
        StringResource.list_editor_error_date_range,
    )

    ListEntryProblem.StartedCleared in this -> UiText.StringResource(
        StringResource.list_editor_error_date_cleared,
    )

    else -> null
}

private fun Set<ListEntryProblem>.finishedError(): UiText? = when {
    ListEntryProblem.FinishedOutOfRange in this -> UiText.StringResource(
        StringResource.list_editor_error_date_range,
    )

    ListEntryProblem.FinishedBeforeStarted in this -> UiText.StringResource(
        StringResource.list_editor_error_finished_before,
    )

    ListEntryProblem.FinishedCleared in this -> UiText.StringResource(
        StringResource.list_editor_error_date_cleared,
    )

    else -> null
}

/** 85 → "8.5". */
fun Int.toVoteLabel(): UiText = UiText.StringValue("%.1f".format(this / VOTE_SCALE))

/** The Material date picker works in UTC milliseconds at the start of the day. */
fun LocalDate.toPickerMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun Long.toPickerDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
