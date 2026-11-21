package com.zc.vndb.feature.userlist.presentation.model

/** A dialog open over the editor. */
sealed interface ListEditorDialog {
    /** Date picker for [field], starting at [initialMillis] (UTC midnight) when it is set. */
    data class PickDate(val field: DateField, val initialMillis: Long?) : ListEditorDialog

    data object ConfirmRemove : ListEditorDialog
}
