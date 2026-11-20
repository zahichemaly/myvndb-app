package com.zc.vndb.feature.browse.presentation.component.searchfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** Inset search bar (DESIGN.md "Input Fields & Search Bars"). */
@Composable
fun SearchFieldView(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = UiText.StringResource(StringResource.browse_search_hint).asString(),
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = VndbTheme.colors.accent,
            )
        },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = UiText.StringResource(
                            StringResource.browse_clear_search,
                        ).asString(),
                    )
                }
            }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboard?.hide()
                onSearch()
            },
        ),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = VndbTheme.colors.surfaceInset,
            unfocusedContainerColor = VndbTheme.colors.surfaceInset,
            focusedBorderColor = VndbTheme.colors.accent,
            unfocusedBorderColor = VndbTheme.colors.borderSubtle,
            focusedPlaceholderColor = VndbTheme.colors.placeholder,
            unfocusedPlaceholderColor = VndbTheme.colors.placeholder,
            cursorColor = VndbTheme.colors.accent,
        ),
    )
}

@AdaptivePreviews
@Composable
private fun SearchFieldViewPreview() {
    VndbTheme {
        SearchFieldView(
            value = "steins",
            onValueChange = {},
            onSearch = {},
            onClear = {},
            modifier = Modifier.fillMaxWidth().padding(VndbTheme.spacing.lg),
        )
    }
}
