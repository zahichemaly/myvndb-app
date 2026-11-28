package com.zc.vndb.feature.detail.presentation.component.taggroups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.component.textlink.TextLinkView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.TintedColors
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteTone
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.ContentNoteUIModel
import com.zc.vndb.feature.detail.presentation.component.taggroups.model.TagGroupUIModel
import com.zc.vndb.feature.detail.presentation.model.TagChipUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * "Tags & Content": tag groups, how many the spoiler filter hides, and content notes. [toggleLabel]
 * ("See all 34" / "Show less") sits in the header when some tags are left out.
 */
@Composable
fun TagGroupsView(
    groups: ImmutableList<TagGroupUIModel>,
    hiddenLabel: UiText?,
    notes: ImmutableList<ContentNoteUIModel>,
    toggleLabel: UiText?,
    onTagClick: (String) -> Unit,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCardView(
        modifier = modifier,
        title = UiText.StringResource(StringResource.detail_tags_and_content),
        trailing = toggleLabel?.let { label ->
            {
                TextLinkView(label = label, onClick = onToggleClick)
            }
        },
    ) {
        groups.forEach { group ->
            GroupTitle(group.title.asString())
            ChipFlow {
                group.tags.forEach { tag ->
                    TagChipView(label = tag.label, onClick = { onTagClick(tag.id) })
                }
            }
        }
        hiddenLabel?.let { hidden ->
            Text(
                text = hidden.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (notes.isNotEmpty()) {
            GroupTitle(UiText.StringResource(StringResource.detail_tags_content_notes).asString())
            ChipFlow {
                notes.forEach { note ->
                    TagChipView(
                        label = note.label,
                        colors = note.tone.colors(),
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = VndbTheme.spacing.xs),
    )
}

@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) { content() }
}

@Composable
private fun ContentNoteTone.colors(): TintedColors = when (this) {
    ContentNoteTone.Safe -> VndbTheme.colors.spoilerNone
    ContentNoteTone.Caution -> VndbTheme.colors.spoilerMinor
    ContentNoteTone.Adult -> VndbTheme.colors.tagNsfw
}

@AdaptivePreviews
@Composable
private fun TagGroupsViewPreview() {
    VndbTheme {
        TagGroupsView(
            groups = persistentListOf(
                TagGroupUIModel(
                    UiText.StringValue("THEMES"),
                    persistentListOf(
                        TagChipUIModel("g1", UiText.StringValue("Time Travel")),
                        TagChipUIModel("g2", UiText.StringValue("Mad Scientist")),
                    ),
                ),
            ),
            hiddenLabel = UiText.StringValue("3 hidden by spoiler filter"),
            toggleLabel = UiText.StringValue("See all 24"),
            onTagClick = {},
            onToggleClick = {},
            notes = persistentListOf(
                ContentNoteUIModel(UiText.StringValue("No sexual content"), ContentNoteTone.Safe),
            ),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
