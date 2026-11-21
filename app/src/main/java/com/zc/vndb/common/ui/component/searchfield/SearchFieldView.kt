package com.zc.vndb.common.ui.component.searchfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/**
 * Compact on purpose: a full Material text field (56dp) dwarfed the chips around it. Controls that
 * sit beside the field (Browse's filter button) use the same height.
 */
val SearchFieldHeight = 44.dp
private val IconSize = 20.dp

/** Inset search bar (DESIGN.md "Input Fields & Search Bars"), showing [placeholder] when empty. */
@Composable
fun SearchFieldView(
    value: String,
    placeholder: UiText,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(SearchFieldHeight),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(VndbTheme.colors.accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboard?.hide()
                onSearch()
            },
        ),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            SearchFieldFrame(
                isEmpty = value.isEmpty(),
                isFocused = isFocused,
                placeholder = placeholder,
                onClear = onClear,
                innerTextField = innerTextField,
            )
        },
    )
}

/** The inset box around the text: search icon, placeholder or text, and the clear button. */
@Composable
private fun SearchFieldFrame(
    isEmpty: Boolean,
    isFocused: Boolean,
    placeholder: UiText,
    onClear: () -> Unit,
    innerTextField: @Composable () -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(VndbTheme.colors.surfaceInset, shape)
            .border(
                width = VndbTheme.dimens.borderWidth,
                color = if (isFocused) VndbTheme.colors.accent else VndbTheme.colors.borderSubtle,
                shape = shape,
            )
            .padding(start = VndbTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = VndbTheme.colors.accent,
            modifier = Modifier.size(IconSize),
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (isEmpty) {
                Text(
                    text = placeholder.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VndbTheme.colors.placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            innerTextField()
        }
        if (!isEmpty) {
            IconButton(onClick = onClear) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = UiText.StringResource(
                        StringResource.search_clear,
                    ).asString(),
                    modifier = Modifier.size(IconSize),
                )
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun SearchFieldViewPreview() {
    VndbTheme {
        SearchFieldView(
            value = "steins",
            placeholder = UiText.StringValue("Search visual novels…"),
            onValueChange = {},
            onSearch = {},
            onClear = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
