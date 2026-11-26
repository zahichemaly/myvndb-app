package com.zc.vndb.feature.settings.presentation.home.component.settingrow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val IconBoxSize = 40.dp
private const val DISABLED_ALPHA = 0.5f

/**
 * A settings row card: icon, title, explanation and a trailing control. With [onClick] the whole
 * row is tappable; a [trailing] value or switch sits on the right. [isDestructive] colors the icon
 * and title as an error (e.g. Log out).
 */
@Composable
fun SettingRowView(
    icon: ImageVector,
    title: UiText,
    body: UiText?,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null,
    role: Role = Role.Button,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .then(
                if (onClick != null && isEnabled) {
                    Modifier.clickable(
                        role = role,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(VndbTheme.spacing.md)
            .alpha(if (isEnabled) 1f else DISABLED_ALPHA),
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(IconBoxSize)
                .clip(MaterialTheme.shapes.medium)
                .background(VndbTheme.colors.surfaceElevated),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else VndbTheme.colors.accent,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        ) {
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
            body?.let {
                Text(
                    text = it.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke()
    }
}

/** A row whose control is a switch; tapping the row flips it. */
@Composable
fun SettingSwitchRowView(
    icon: ImageVector,
    title: UiText,
    body: UiText?,
    isOn: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    SettingRowView(
        icon = icon,
        title = title,
        body = body,
        modifier = modifier,
        isEnabled = isEnabled,
        onClick = { onChange(!isOn) },
        role = Role.Switch,
    ) {
        Switch(
            checked = isOn,
            onCheckedChange = onChange,
            enabled = isEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VndbTheme.colors.onAccent,
                checkedTrackColor = VndbTheme.colors.accent,
            ),
        )
    }
}

/** The current value of a choice row; the row itself is what the user taps. */
@Composable
fun SettingValueView(value: UiText, modifier: Modifier = Modifier) {
    Text(
        text = value.asString(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@AdaptivePreviews
@Composable
private fun SettingRowViewPreview() {
    VndbTheme {
        Column(verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
            SettingRowView(
                icon = Icons.Outlined.Translate,
                title = UiText.StringValue("Title language"),
                body = UiText.StringValue("Which title comes first on cards and pages."),
                onClick = {},
            ) { SettingValueView(UiText.StringValue("Romanized")) }
            SettingSwitchRowView(
                icon = Icons.Outlined.Translate,
                title = UiText.StringValue("Include 18+ titles in Browse"),
                body = UiText.StringValue("Browse starts with every age rating."),
                isOn = true,
                onChange = {},
            )
        }
    }
}
