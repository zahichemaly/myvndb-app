package com.zc.vndb.feature.detail.presentation.component.staffrole

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.component.staffrole.model.StaffPersonUIModel
import com.zc.vndb.feature.detail.presentation.component.staffrole.model.StaffRoleUIModel
import kotlinx.collections.immutable.persistentListOf

private val DefaultLabelWidth = 112.dp

/** "Scenario:  Hino Yuuki, Hayase Ryou"; each name opens that person's staff page. */
@Composable
fun StaffRoleView(
    role: StaffRoleUIModel,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelWidth: Dp = DefaultLabelWidth,
) {
    Row(modifier = modifier) {
        Text(
            text = role.label.asString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(labelWidth),
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs)) {
            role.people.forEachIndexed { index, person ->
                val text = person.note?.let { "${person.name} ($it)" } ?: person.name
                Text(
                    text = if (index < role.people.lastIndex) "$text," else text,
                    style = MaterialTheme.typography.labelSmall,
                    color = VndbTheme.colors.accent,
                    modifier = Modifier.clickable(role = Role.Button) { onPersonClick(person.id) },
                )
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun StaffRoleViewPreview() {
    VndbTheme {
        StaffRoleView(
            role = StaffRoleUIModel(
                label = UiText.StringValue("Scenario"),
                people = persistentListOf(
                    StaffPersonUIModel("s1", "Hino Yuuki", null),
                    StaffPersonUIModel("s2", "Hayase Ryou", "Route B"),
                ),
            ),
            onPersonClick = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
