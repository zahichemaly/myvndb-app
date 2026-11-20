package com.zc.vndb.common.ui.component.languagepills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.languagepills.model.LanguagePillUIModel
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** "🇬🇧 English" pills for the languages a novel is available in that the user chose to see. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguagePillsView(pills: ImmutableList<LanguagePillUIModel>, modifier: Modifier = Modifier) {
    if (pills.isEmpty()) return
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        pills.forEach { pill ->
            TagChipView(
                label = UiText.StringValue(pill.label),
                contentDescription = UiText.StringResource(
                    StringResource.language_pill_available_in,
                    listOf(pill.name),
                ),
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun LanguagePillsViewPreview() {
    VndbTheme {
        LanguagePillsView(
            pills = persistentListOf(
                LanguagePillUIModel("en", "🇬🇧", "English"),
                LanguagePillUIModel("ca", null, "Catalan"),
            ),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
