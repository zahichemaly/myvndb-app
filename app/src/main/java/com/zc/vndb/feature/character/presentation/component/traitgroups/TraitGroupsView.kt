package com.zc.vndb.feature.character.presentation.component.traitgroups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.character.presentation.component.traitgroups.model.TraitGroupUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** "Traits": one chip group per VNDB trait group, plus how many the spoiler filter hides. */
@Composable
fun TraitGroupsView(
    groups: ImmutableList<TraitGroupUIModel>,
    hiddenLabel: UiText?,
    modifier: Modifier = Modifier,
) {
    SectionCardView(
        modifier = modifier,
        title = UiText.StringResource(StringResource.character_traits),
    ) {
        groups.forEach { group ->
            Text(
                text = group.title.asString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = VndbTheme.spacing.xs),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                group.traits.forEach { trait -> TagChipView(label = trait) }
            }
        }
        hiddenLabel?.let { hidden ->
            Text(
                text = hidden.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun TraitGroupsViewPreview() {
    VndbTheme {
        TraitGroupsView(
            groups = persistentListOf(
                TraitGroupUIModel(
                    UiText.StringValue("HAIR"),
                    persistentListOf(UiText.StringValue("Long"), UiText.StringValue("Red")),
                ),
            ),
            hiddenLabel = UiText.StringValue("4 hidden by spoiler filter"),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
