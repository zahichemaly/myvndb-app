package com.zc.vndb.app.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews

/**
 * A tab's height: icon over label. With the toolbar's vertical padding it fills the toolbar's
 * standard 64dp, which the tab screens' `LocalTabToolbarPadding` counts on.
 */
private val TabHeight = 56.dp

/** Keeps the tabs close to one width, whatever their label. */
private val TabMinWidth = 72.dp

/**
 * The tabs in a Material 3 floating toolbar: each an icon with its label below, the selected one
 * in an accent pill. A solid surface with a hairline edge, like the other navigation surfaces.
 */
@Composable
internal fun TabToolbar(
    selectedTab: NavKey,
    onSelectTab: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, CircleShape)
            .selectableGroup(),
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors().copy(
            toolbarContainerColor = VndbTheme.colors.surfaceInset,
            toolbarContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(
            horizontal = VndbTheme.spacing.sm,
            vertical = VndbTheme.spacing.xs,
        ),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
            TopLevelDestination.entries.forEach { destination ->
                TabItem(
                    destination = destination,
                    isSelected = selectedTab == destination.key,
                    onClick = { onSelectTab(destination.key) },
                )
            }
        }
    }
}

@Composable
private fun TabItem(destination: TopLevelDestination, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) VndbTheme.colors.accent else Color.Transparent,
        label = "tabContainer",
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            VndbTheme.colors.onAccent
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "tabContent",
    )
    Column(
        modifier = Modifier
            .height(TabHeight)
            .widthIn(min = TabMinWidth)
            .clip(CircleShape)
            .background(containerColor)
            .selectable(selected = isSelected, onClick = onClick, role = Role.Tab)
            .padding(horizontal = VndbTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // The label below names the tab.
        Icon(imageVector = destination.icon, contentDescription = null, tint = contentColor)
        Text(
            text = destination.label.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@AdaptivePreviews
@Composable
private fun TabToolbarPreview() {
    VndbTheme {
        TabToolbar(selectedTab = TopLevelDestination.Discover.key, onSelectTab = {})
    }
}
