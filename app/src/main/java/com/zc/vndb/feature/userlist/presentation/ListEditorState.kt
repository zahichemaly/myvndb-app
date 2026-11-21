package com.zc.vndb.feature.userlist.presentation

import androidx.compose.runtime.Immutable
import com.zc.vndb.common.ui.component.error.model.ErrorUIModel
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.feature.userlist.presentation.model.LabelOptionUIModel
import com.zc.vndb.feature.userlist.presentation.model.ListEditorDialog
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ListEditorState(
    /** The visual novel's title, shown under the sheet's heading. */
    val novelTitle: UiText,
    val isLoading: Boolean = true,
    /** Loading the entry or the labels failed. */
    val error: ErrorUIModel? = null,
    /** The VN is already on the list: the sheet offers Remove. */
    val isOnList: Boolean = false,
    /** Playing, Finished, Stalled, Dropped (one at a time). */
    val statuses: ImmutableList<LabelOptionUIModel> = persistentListOf(),
    /** Wishlist, Blacklist and the user's own labels. */
    val otherLabels: ImmutableList<LabelOptionUIModel> = persistentListOf(),
    /** 10–100, or null without a vote. */
    val vote: Int? = null,
    /** "No vote" until the slider moves, then the vote itself: "8.5". */
    val voteLabel: UiText = UiText.StringValue(""),
    val startedLabel: UiText? = null,
    val finishedLabel: UiText? = null,
    /** Only a date that isn't saved yet can be cleared: VNDB can change a date but not remove it. */
    val canClearStarted: Boolean = false,
    val canClearFinished: Boolean = false,
    val startedError: UiText? = null,
    val finishedError: UiText? = null,
    /** Shown under the dates when one is saved: explains why it has no clear button. */
    val datesHint: UiText? = null,
    val notes: String = "",
    /** "12 / 2000". */
    val notesCounter: UiText = UiText.StringValue(""),
    val notesError: UiText? = null,
    /** Nothing VNDB would reject: Save is enabled. */
    val isValid: Boolean = true,
    val isSaving: Boolean = false,
    /** Saving or removing failed; the edits are kept. */
    val saveError: UiText? = null,
    val dialog: ListEditorDialog? = null,
)
