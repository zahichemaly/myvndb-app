package com.zc.vndb.feature.discover.presentation.home.component.sectionheader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
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
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

private val IconSize = 18.dp

/**
 * Section title with a leading accent icon, an optional line under it ([subtitle]) and an
 * optional "See all" link at the end.
 */
@Composable
fun SectionHeaderView(
    title: UiText,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: UiText? = null,
    onSeeAllClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VndbTheme.colors.accent,
                modifier = Modifier.size(IconSize),
            )
            Text(
                text = title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            onSeeAllClick?.let { SeeAllLink(onClick = it) }
        }
        subtitle?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // Lined up with the title, past the icon.
                modifier = Modifier.padding(start = IconSize + VndbTheme.spacing.sm),
            )
        }
    }
}

/**
 * Only as tall as its text, so a header with it is as tall as one without and every section sits
 * the same distance below the one above. Compose still extends its touch target to the 48dp
 * minimum when hit-testing.
 */
@Composable
private fun SeeAllLink(onClick: () -> Unit) {
    Text(
        text = UiText.StringResource(StringResource.discover_see_all).asString(),
        style = MaterialTheme.typography.labelLarge,
        color = VndbTheme.colors.accent,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VndbTheme.spacing.sm, vertical = VndbTheme.spacing.xs),
    )
}

@AdaptivePreviews
@Composable
private fun SectionHeaderViewPreview() {
    VndbTheme {
        SectionHeaderView(
            title = UiText.StringValue("New & Trending"),
            icon = Icons.Outlined.Bolt,
            subtitle = UiText.StringValue("English, Japanese"),
            onSeeAllClick = {},
        )
    }
}
