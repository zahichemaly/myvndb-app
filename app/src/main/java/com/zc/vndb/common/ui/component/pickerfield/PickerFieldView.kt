package com.zc.vndb.common.ui.component.pickerfield

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/**
 * A compact outlined field: a small [label] above its [value], the whole box opening a picker
 * (a date, a language). Two fit side by side on a row. [trailing], such as a clear button, sits at
 * the end; an [isError] field gets an error border. At least 48dp tall, so it is easy to tap.
 */
@Composable
fun PickerFieldView(
    label: UiText,
    value: UiText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isError: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.small
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = LocalMinimumInteractiveComponentSize.current)
            .clip(shape)
            .border(
                width = VndbTheme.dimens.borderWidth,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = VndbTheme.spacing.md),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = VndbTheme.spacing.xs)
                .padding(end = if (trailing == null) VndbTheme.spacing.md else 0.dp),
        ) {
            Text(
                text = label.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        trailing?.invoke()
    }
}

@AdaptivePreviews
@Composable
private fun PickerFieldViewPreview() {
    VndbTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        ) {
            PickerFieldView(
                label = UiText.StringValue("Started"),
                value = UiText.StringValue("2026-11-01"),
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            PickerFieldView(
                label = UiText.StringValue("Finished"),
                value = UiText.StringValue("Set date"),
                onClick = {},
                modifier = Modifier.weight(1f),
                valueColor = MaterialTheme.colorScheme.primary,
                isError = true,
            )
        }
    }
}
