package com.zc.vndb.common.ui.component.textlink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val IconSize = 16.dp

/**
 * An accent link at the end of a header ("See all", "Edit"), with an optional leading [icon].
 * Only as tall as its text, so a header with it is as tall as one without and every title sits
 * the same distance from the top. Compose still extends its touch target to the 48dp minimum when
 * hit-testing.
 */
@Composable
fun TextLinkView(
    label: UiText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VndbTheme.spacing.sm, vertical = VndbTheme.spacing.xs),
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = VndbTheme.colors.accent,
                modifier = Modifier.size(IconSize),
            )
        }
        Text(
            text = label.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = VndbTheme.colors.accent,
        )
    }
}

@AdaptivePreviews
@Composable
private fun TextLinkViewPreview() {
    VndbTheme {
        Column(modifier = Modifier.padding(VndbTheme.spacing.lg)) {
            TextLinkView(label = UiText.StringValue("See all"), onClick = {})
            TextLinkView(
                label = UiText.StringValue("Edit"),
                onClick = {},
                icon = Icons.Outlined.Edit,
            )
        }
    }
}
