package com.zc.vndb.common.ui.component.platformicon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.platformicon.mapper.toImageVector
import com.zc.vndb.common.ui.component.platformicon.model.PlatformIcon
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val IconSize = 16.dp

/**
 * Platform logos in a row, then [moreLabel] ("+2") for the ones left out. Screen readers hear
 * [contentDescription] ("Windows, Linux"); without one the logos are silent, for rows whose text
 * already names the platforms.
 */
@Composable
fun PlatformIconsView(
    icons: ImmutableList<PlatformIcon>,
    modifier: Modifier = Modifier,
    moreLabel: UiText? = null,
    contentDescription: UiText? = null,
) {
    if (icons.isEmpty()) return
    val description = contentDescription?.asString()
    Row(
        modifier = modifier.clearAndSetSemantics {
            description?.let { this.contentDescription = it }
        },
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icons.forEach { icon ->
            Icon(
                imageVector = icon.toImageVector(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(IconSize),
            )
        }
        moreLabel?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun PlatformIconsViewPreview() {
    VndbTheme {
        PlatformIconsView(
            icons = persistentListOf(PlatformIcon.Windows, PlatformIcon.Linux, PlatformIcon.Apple),
            moreLabel = UiText.StringValue("+1"),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
