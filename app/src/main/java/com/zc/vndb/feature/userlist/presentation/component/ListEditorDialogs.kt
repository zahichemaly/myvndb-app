package com.zc.vndb.feature.userlist.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.feature.userlist.domain.model.MyListEntry
import com.zc.vndb.feature.userlist.presentation.ListEditorIntent
import com.zc.vndb.feature.userlist.presentation.mapper.toPickerMillis
import com.zc.vndb.feature.userlist.presentation.model.ListEditorDialog
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** The editor's date picker and the Remove confirmation. */
@Composable
fun ListEditorDialogs(dialog: ListEditorDialog?, onIntent: (ListEditorIntent) -> Unit) {
    when (dialog) {
        is ListEditorDialog.PickDate -> PickDateDialog(dialog, onIntent)

        ListEditorDialog.ConfirmRemove -> AlertDialog(
            onDismissRequest = { onIntent(ListEditorIntent.DialogDismissed) },
            title = {
                Text(
                    UiText.StringResource(StringResource.list_editor_remove_title).asString(),
                )
            },
            text = {
                Text(
                    UiText.StringResource(StringResource.list_editor_remove_body).asString(),
                )
            },
            confirmButton = {
                TextButton(onClick = { onIntent(ListEditorIntent.RemoveConfirmed) }) {
                    Text(
                        text = UiText.StringResource(StringResource.list_editor_remove).asString(),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(ListEditorIntent.DialogDismissed) }) {
                    Text(UiText.StringResource(StringResource.action_cancel).asString())
                }
            },
            containerColor = VndbTheme.colors.surfaceCard,
        )

        null -> Unit
    }
}

/** VNDB takes reading dates from 1970-01-01; a date can't be in the future either. */
@OptIn(ExperimentalMaterial3Api::class)
private object PastOrToday : SelectableDates {
    private val minMillis = MyListEntry.MIN_DATE.toPickerMillis()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis in minMillis..System.currentTimeMillis()

    override fun isSelectableYear(year: Int): Boolean =
        year in MyListEntry.MIN_DATE.year..Clock.System.todayIn(
            TimeZone.currentSystemDefault(),
        ).year
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickDateDialog(
    dialog: ListEditorDialog.PickDate,
    onIntent: (ListEditorIntent) -> Unit,
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = dialog.initialMillis,
        selectableDates = PastOrToday,
    )
    DatePickerDialog(
        onDismissRequest = { onIntent(ListEditorIntent.DialogDismissed) },
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onIntent(ListEditorIntent.DatePicked(dialog.field, millis))
                    } ?: onIntent(ListEditorIntent.DialogDismissed)
                },
            ) { Text(UiText.StringResource(StringResource.action_ok).asString()) }
        },
        dismissButton = {
            TextButton(onClick = { onIntent(ListEditorIntent.DialogDismissed) }) {
                Text(UiText.StringResource(StringResource.action_cancel).asString())
            }
        },
    ) { DatePicker(state = pickerState) }
}
