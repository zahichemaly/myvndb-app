package com.zc.vndb.feature.settings.presentation.component.choicedialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.settings.presentation.model.SettingOptionUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** Tall enough to space the options apart and give each a full-width touch target. */
private val OptionRowMinHeight = 48.dp
private val OptionIconSize = 20.dp

/** Single choice: picking an option applies it and closes the dialog. */
@Composable
fun ChoiceDialogView(
    title: UiText,
    options: ImmutableList<SettingOptionUIModel>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(title.asString()) },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = OptionRowMinHeight)
                            .selectable(
                                selected = option.isSelected,
                                onClick = { onSelect(option.key) },
                                role = Role.RadioButton,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = option.isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VndbTheme.colors.accent,
                            ),
                        )
                        option.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(OptionIconSize),
                            )
                        }
                        Text(option.label.asString())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(UiText.StringResource(StringResource.action_cancel).asString())
            }
        },
        containerColor = VndbTheme.colors.surfaceCard,
    )
}

@AdaptivePreviews
@Composable
private fun ChoiceDialogViewPreview() {
    VndbTheme {
        ChoiceDialogView(
            title = UiText.StringValue("Title language"),
            options = persistentListOf(
                SettingOptionUIModel(
                    "Romanized",
                    UiText.StringValue("Romanized"),
                    isSelected = true,
                ),
                SettingOptionUIModel(
                    "Original",
                    UiText.StringValue("Japanese"),
                    isSelected = false,
                ),
            ),
            onSelect = {},
            onDismiss = {},
        )
    }
}

@AdaptivePreviews
@Composable
private fun ChoiceDialogViewIconsPreview() {
    VndbTheme {
        ChoiceDialogView(
            title = UiText.StringValue("Default spoiler level"),
            options = persistentListOf(
                SettingOptionUIModel(
                    "None",
                    UiText.StringValue("Off"),
                    isSelected = true,
                    icon = Icons.Outlined.VisibilityOff,
                ),
                SettingOptionUIModel(
                    "Minor",
                    UiText.StringValue("Minor"),
                    isSelected = false,
                    icon = Icons.Outlined.Visibility,
                ),
            ),
            onSelect = {},
            onDismiss = {},
        )
    }
}
