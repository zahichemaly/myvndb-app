package com.zc.vndb.common.ui.component.confirmdialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** A yes/no question: [confirm] runs [onConfirm], Cancel (or tapping outside) runs [onDismiss]. */
@Composable
fun ConfirmDialogView(
    title: UiText,
    body: UiText,
    confirm: UiText,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(title.asString()) },
        text = { Text(body.asString()) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirm.asString(), color = VndbTheme.colors.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(UiText.StringResource(StringResource.action_cancel).asString())
            }
        },
        containerColor = VndbTheme.colors.surfaceCard,
    )
}

@AdaptivePreviews
@Composable
private fun ConfirmDialogViewPreview() {
    VndbTheme {
        ConfirmDialogView(
            title = UiText.StringValue("Show this cover?"),
            body = UiText.StringValue("This cover may contain sexual or violent content."),
            confirm = UiText.StringValue("I'm 18 or older"),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
