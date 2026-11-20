package com.zc.vndb.feature.browse.presentation.component.filtersheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.LanguageOption
import com.zc.vndb.common.ui.mapper.displayName
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Tall enough to space the options apart and give each a full-width touch target. */
private val OptionRowMinHeight = 48.dp

/**
 * Single choice for the "Original language" filter: "Any" or one language the novel was first
 * written in. The pick goes back on OK; Cancel leaves the filter as it was.
 */
@Composable
fun OriginalLanguageDialogView(
    options: ImmutableList<LanguageOption>,
    selected: String?,
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pick: String? by rememberSaveable { mutableStateOf(selected) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                UiText.StringResource(StringResource.browse_filter_original_language).asString(),
            )
        },
        text = {
            Column {
                Text(
                    text = UiText.StringResource(StringResource.browse_original_language_hint)
                        .asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LanguageRadioList(options, pick) { pick = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pick) }) {
                Text(UiText.StringResource(StringResource.action_ok).asString())
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

@Composable
private fun LanguageRadioList(
    options: ImmutableList<LanguageOption>,
    pick: String?,
    onPick: (String?) -> Unit,
) {
    val any = UiText.StringResource(StringResource.browse_filter_any).asString()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .selectableGroup(),
    ) {
        RadioRow(label = any, isSelected = pick == null, onClick = { onPick(null) })
        options.forEach { option ->
            RadioRow(
                label = option.displayName(),
                isSelected = pick == option.code,
                onClick = { onPick(option.code) },
            )
        }
    }
}

@Composable
private fun RadioRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = VndbTheme.colors.accent),
        )
        Text(label)
    }
}

@AdaptivePreviews
@Composable
private fun OriginalLanguageDialogViewPreview() {
    VndbTheme {
        OriginalLanguageDialogView(
            options = persistentListOf(
                LanguageOption("en", "English"),
                LanguageOption("ja", "Japanese"),
                LanguageOption("ko", "Korean"),
            ),
            selected = "ja",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
