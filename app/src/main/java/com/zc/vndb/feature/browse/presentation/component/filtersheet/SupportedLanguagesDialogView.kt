package com.zc.vndb.feature.browse.presentation.component.filtersheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
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
import com.zc.vndb.common.ui.mapper.languageName
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.browse.domain.model.SupportedLanguageChoices

/** Tall enough to space the options apart and give each a full-width touch target. */
private val OptionRowMinHeight = 48.dp

/**
 * Multiple choice for the "Supported languages" filter: a novel matches when it has a release in
 * any chosen language. The picks go back on OK; none chosen means no language filter.
 */
@Composable
fun SupportedLanguagesDialogView(
    selected: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A List (an ArrayList at runtime) so it survives configuration changes.
    var picks: List<String> by rememberSaveable { mutableStateOf(selected.toList()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                UiText.StringResource(StringResource.browse_filter_supported_languages).asString(),
            )
        },
        text = {
            Column {
                Text(
                    text = UiText.StringResource(StringResource.browse_supported_languages_hint)
                        .asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LanguageOptions(picks) { picks = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(picks.toSet()) }) {
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
private fun LanguageOptions(picks: List<String>, onChange: (List<String>) -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        SupportedLanguageChoices.forEach { code ->
            val isChecked = code in picks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = OptionRowMinHeight)
                    .toggleable(
                        value = isChecked,
                        role = Role.Checkbox,
                        onValueChange = { checked ->
                            onChange(if (checked) picks + code else picks - code)
                        },
                    ),
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = VndbTheme.colors.accent),
                )
                Text(languageName(code))
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun SupportedLanguagesDialogViewPreview() {
    VndbTheme {
        SupportedLanguagesDialogView(
            selected = setOf("en", "ja"),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
