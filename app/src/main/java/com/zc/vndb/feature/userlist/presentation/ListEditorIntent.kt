package com.zc.vndb.feature.userlist.presentation

import com.zc.vndb.feature.userlist.presentation.model.DateField

sealed interface ListEditorIntent {
    data object Retry : ListEditorIntent

    /** Tapping the selected status again clears it. */
    data class StatusSelected(val labelId: Int) : ListEditorIntent
    data class LabelToggled(val labelId: Int) : ListEditorIntent

    /** [vote] is 10–100. */
    data class VoteChanged(val vote: Int) : ListEditorIntent
    data object VoteCleared : ListEditorIntent
    data class DateClicked(val field: DateField) : ListEditorIntent

    /** [millis] is the picked day at UTC midnight, as the Material date picker reports it. */
    data class DatePicked(val field: DateField, val millis: Long) : ListEditorIntent
    data class DateCleared(val field: DateField) : ListEditorIntent
    data class NotesChanged(val notes: String) : ListEditorIntent
    data object SaveClicked : ListEditorIntent
    data object RemoveClicked : ListEditorIntent
    data object RemoveConfirmed : ListEditorIntent
    data object DialogDismissed : ListEditorIntent
}
