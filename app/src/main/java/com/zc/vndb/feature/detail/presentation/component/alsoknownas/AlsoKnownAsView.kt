package com.zc.vndb.feature.detail.presentation.component.alsoknownas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.ui.component.chip.TagChipView
import com.zc.vndb.common.ui.component.sectioncard.SectionCardView
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.detail.presentation.model.AlsoKnownAsUIModel
import com.zc.vndb.feature.detail.presentation.model.TitleLineUIModel
import kotlinx.collections.immutable.persistentListOf

private val LanguageWidth = 96.dp

/** Titles in other languages, then aliases as chips. */
@Composable
fun AlsoKnownAsView(model: AlsoKnownAsUIModel, modifier: Modifier = Modifier) {
    SectionCardView(
        modifier = modifier,
        title = UiText.StringResource(StringResource.detail_also_known_as),
    ) {
        model.titles.forEach { line ->
            Row {
                Text(
                    text = line.language.asString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(LanguageWidth),
                )
                Column {
                    Text(
                        text = line.title.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    line.latin?.let { latin ->
                        Text(
                            text = latin.asString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        if (model.aliases.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(top = VndbTheme.spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            ) {
                model.aliases.forEach { alias -> TagChipView(label = alias) }
            }
        }
    }
}

@AdaptivePreviews
@Composable
private fun AlsoKnownAsViewPreview() {
    VndbTheme {
        AlsoKnownAsView(
            model = AlsoKnownAsUIModel(
                titles = persistentListOf(
                    TitleLineUIModel(
                        UiText.StringValue("Chinese"),
                        UiText.StringValue("淑女同萌！"),
                        null,
                    ),
                ),
                aliases = persistentListOf(
                    UiText.StringValue("ハロレ"),
                    UiText.StringValue("Hello, Lady"),
                ),
            ),
            modifier = Modifier.padding(VndbTheme.spacing.lg),
        )
    }
}
