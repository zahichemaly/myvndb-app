package com.zc.vndb.feature.browse.presentation.component.filterbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/** Same height as the search field it sits beside. */
private val ButtonSize = 56.dp
private val BadgeSize = 16.dp

/** The filter icon next to the search field; with filters on it turns accent and shows how many. */
@Composable
fun FilterButtonView(activeCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = UiText.StringResource(StringResource.browse_filters_button).asString()
    val shape = MaterialTheme.shapes.medium
    val isActive = activeCount > 0
    Box(
        modifier = modifier
            .size(ButtonSize)
            .clip(shape)
            .background(VndbTheme.colors.surfaceInset)
            .border(
                VndbTheme.dimens.borderWidth,
                if (isActive) VndbTheme.colors.accent else VndbTheme.colors.borderSubtle,
                shape,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.FilterList,
            contentDescription = null,
            tint = if (isActive) VndbTheme.colors.accent else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(VndbTheme.spacing.xs)
                    .size(BadgeSize)
                    .background(VndbTheme.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = activeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = VndbTheme.colors.onAccent,
                )
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun FilterButtonViewPreview() {
    VndbTheme {
        Box(modifier = Modifier.padding(VndbTheme.spacing.lg)) {
            FilterButtonView(activeCount = 3, onClick = {})
        }
    }
}
