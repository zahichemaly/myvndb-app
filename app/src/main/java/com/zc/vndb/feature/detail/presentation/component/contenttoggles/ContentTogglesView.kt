package com.zc.vndb.feature.detail.presentation.component.contenttoggles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.selectablechip.SelectableChipView
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.ContentFilter
import com.zc.vndb.feature.detail.presentation.model.ContentToggleUIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** The vndb.org screenshot toggles: one row for sexual levels, one for violence levels. */
@Composable
fun ContentTogglesView(
    sexual: ImmutableList<ContentToggleUIState>,
    violence: ImmutableList<ContentToggleUIState>,
    onToggle: (ContentFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm)) {
        listOf(sexual, violence).forEach { row ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                row.forEach { toggle ->
                    SelectableChipView(
                        label = toggle.label,
                        isSelected = toggle.isSelected,
                        onClick = { onToggle(toggle.filter) },
                    )
                }
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun ContentTogglesViewPreview() {
    VndbTheme {
        ContentTogglesView(
            sexual = persistentListOf(
                ContentToggleUIState(
                    ContentFilter.Safe,
                    UiText.StringValue("Safe 8"),
                    isSelected = true,
                ),
                ContentToggleUIState(
                    ContentFilter.Suggestive,
                    UiText.StringValue("Suggestive 1"),
                    isSelected = false,
                ),
            ),
            violence = persistentListOf(
                ContentToggleUIState(
                    ContentFilter.Tame,
                    UiText.StringValue("Tame 9"),
                    isSelected = true,
                ),
            ),
            onToggle = {},
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
